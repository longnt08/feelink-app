package com.example.diaryapp.data.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SyncManager {
    private static final String TAG = "SyncManager";
    private static final String PREF_NAME = "diary_sync_prefs";
    
    private final Context context;
    private final EntryDao entryDao;
    private final UserDao userDao;
    private final FirestoreRepository firestoreRepository;
    private final long localUserId;
    private final String firebaseUserId;
    private final SharedPreferences prefs;
    
    public SyncManager(Context context, long localUserId, String firebaseUserId) {
        this.context = context.getApplicationContext(); // Lưu application context để tránh memory leak
        DiaryDatabase db = DiaryDatabase.getInstance(context);
        this.entryDao = db.entryDao();
        this.userDao = db.userDao();
        this.localUserId = localUserId;
        this.firebaseUserId = firebaseUserId;
        this.firestoreRepository = new FirestoreRepository(firebaseUserId);
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public Task<SyncResult> performSync() {
        final TaskCompletionSource<SyncResult> syncTask = new TaskCompletionSource<>();
        final SyncResult result = new SyncResult();
        
        // 1. Tải lên thay đổi cục bộ
        uploadLocalChanges()
            .continueWithTask(task -> {
                if (task.isSuccessful()) {
                    result.localUploaded = task.getResult();
                    Log.d(TAG, "Uploaded " + result.localUploaded + " entries");
                } else {
                    Log.e(TAG, "Error during upload", task.getException());
                }
                // 2. Tải xuống thay đổi từ remote
                return downloadRemoteChanges();
            })
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    result.remoteDownloaded = task.getResult();
                    result.success = true;
                    Log.d(TAG, "Downloaded " + result.remoteDownloaded + " entries");
                    
                    // Cập nhật timestamp đồng bộ cuối cùng
                    prefs.edit()
                        .putLong("last_sync_" + localUserId, System.currentTimeMillis())
                        .apply();
                } else {
                    result.success = false;
                    result.error = task.getException();
                    Log.e(TAG, "Error during download", task.getException());
                }
                syncTask.setResult(result);
            });
            
        return syncTask.getTask();
    }
    
    private Task<Integer> uploadLocalChanges() {
        TaskCompletionSource<Integer> uploadTask = new TaskCompletionSource<>();
        
        new Thread(() -> {
            try {
                int count = 0;
                
                // Tải lên các entries đã sửa đổi
                List<Entry> unsyncedEntries = entryDao.getUnsyncedEntries(localUserId);
                for (Entry entry : unsyncedEntries) {
                    try {
                        Tasks.await(firestoreRepository.uploadEntry(entry));
                        entryDao.markAsSynced(entry.id, entry.getFirebaseId(), System.currentTimeMillis());
                        count++;
                    } catch (Exception e) {
                        Log.e(TAG, "Error uploading entry", e);
                    }
                }
                
                // Tải lên entries đã xóa
                List<Entry> deletedEntries = entryDao.getDeletedUnsyncedEntries(localUserId);
                for (Entry entry : deletedEntries) {
                    try {
                        Tasks.await(firestoreRepository.deleteEntry(entry));
                        entryDao.markAsSynced(entry.id, entry.getFirebaseId(), System.currentTimeMillis());
                        count++;
                    } catch (Exception e) {
                        Log.e(TAG, "Error syncing deleted entry", e);
                    }
                }
                
                uploadTask.setResult(count);
            } catch (Exception e) {
                uploadTask.setException(e);
            }
        }).start();
        
        return uploadTask.getTask();
    }
    
    private Task<Integer> downloadRemoteChanges() {
        TaskCompletionSource<Integer> downloadTask = new TaskCompletionSource<>();
        
        long lastSyncTime = prefs.getLong("last_sync_" + localUserId, 0);
        
        // Di chuyển kiểm tra user vào background thread
        new Thread(() -> {
            try {
                // Kiểm tra user tồn tại trong background thread
                final boolean userExists = userDao.getUserById((int) localUserId) != null;
                
                if (!userExists) {
                    Log.e(TAG, "Không thể đồng bộ: User ID " + localUserId + " không tồn tại trong cơ sở dữ liệu cục bộ");
                    downloadTask.setException(new Exception("User ID " + localUserId + " không tồn tại trong cơ sở dữ liệu cục bộ"));
                    return;
                }
                
                // Nếu user tồn tại, tiếp tục với quá trình đồng bộ
                continueWithDownload(downloadTask, lastSyncTime);
            } catch (Exception e) {
                Log.w(TAG, "Không thể kiểm tra user tồn tại: " + e.getMessage());
                // Tiếp tục với quá trình đồng bộ dù không kiểm tra được user
                continueWithDownload(downloadTask, lastSyncTime);
            }
        }).start();
        
        return downloadTask.getTask();
    }
    
    // Phương thức mới để xử lý đồng bộ sau khi đã kiểm tra user
    private void continueWithDownload(TaskCompletionSource<Integer> downloadTask, long lastSyncTime) {
        firestoreRepository.getEntriesUpdatedSince(lastSyncTime)
            .addOnSuccessListener(querySnapshot -> {
                new Thread(() -> {
                    try {
                        int count = 0;
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Entry remoteEntry = firestoreRepository.convertDocumentToEntry(document);
                            if (remoteEntry == null) continue;
                            
                            // Đặt userId phù hợp cho entry từ Firebase
                            remoteEntry.userId = localUserId;
                            
                            // Kiểm tra xem entry đã tồn tại chưa (theo firebaseId)
                            Entry existingEntry = null;
                            for (Entry entry : entryDao.getEntriesByUserId(localUserId)) {
                                if (document.getId().equals(entry.getFirebaseId())) {
                                    existingEntry = entry;
                                    break;
                                }
                            }
                            
                            if (existingEntry != null) {
                                // Entry đã tồn tại, cần kiểm tra thời gian cập nhật để giải quyết xung đột
                                if (remoteEntry.getUpdatedAt() > existingEntry.getUpdatedAt()) {
                                    // Remote entry mới hơn, cập nhật local
                                    if (remoteEntry.isDeleted()) {
                                        // Xóa cục bộ nếu đã xóa trên server
                                        entryDao.deleteEntry(existingEntry.id);
                                    } else {
                                        remoteEntry.id = existingEntry.id;
                                        remoteEntry.setLastSyncedAt(System.currentTimeMillis());
                                        remoteEntry.setSynced(true);
                                        entryDao.updateEntry(remoteEntry);
                                    }
                                }
                            } else {
                                // Entry chưa tồn tại cục bộ, thêm mới nếu chưa bị xóa
                                if (!remoteEntry.isDeleted()) {
                                    try {
                                        remoteEntry.setLastSyncedAt(System.currentTimeMillis());
                                        remoteEntry.setSynced(true);
                                        long newId = entryDao.insertEntry(remoteEntry);
                                        remoteEntry.setId(newId);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Lỗi khi thêm entry từ Firestore: " + e.getMessage() + 
                                                  ", Entry: " + remoteEntry.getTitle() + ", UserID: " + remoteEntry.userId);
                                    }
                                }
                            }
                            count++;
                        }
                        downloadTask.setResult(count);
                    } catch (Exception e) {
                        downloadTask.setException(e);
                    }
                }).start();
            })
            .addOnFailureListener(downloadTask::setException);
    }
}