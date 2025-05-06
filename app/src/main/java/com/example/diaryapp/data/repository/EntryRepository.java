package com.example.diaryapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingWorkPolicy;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.EntryDao;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.sync.FirestoreRepository;
import com.example.diaryapp.data.sync.SyncWorker;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EntryRepository {
    private static final String TAG = "EntryRepository";
    private static final String PREF_NAME = "DiaryAppPrefs";
    
    private final EntryDao entryDao;
    private final FirestoreRepository firestoreRepository;
    private final Context context;
    private final String firebaseUserId;

    public EntryRepository(Context context) {
        DiaryDatabase diaryDatabase = DiaryDatabase.getInstance(context);
        entryDao = diaryDatabase.entryDao();
        this.context = context;
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.firebaseUserId = prefs.getString("firebase_user_id", "");
        
        if (!firebaseUserId.isEmpty()) {
            this.firestoreRepository = new FirestoreRepository(firebaseUserId);
        } else {
            this.firestoreRepository = null;
            Log.w(TAG, "No Firebase user ID available, sync functionality disabled");
        }
    }

    public void insertDiary(Entry entry) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Kiểm tra người dùng tồn tại trước khi thêm entry
                DiaryDatabase db = DiaryDatabase.getInstance(context);
                if (db.userDao().getUserById((int) entry.userId) == null) {
                    Log.e(TAG, "Insert failed: User with ID " + entry.userId + " doesn't exist");
                    return; // Không tiếp tục nếu user không tồn tại
                }
                
                Log.d(TAG, "Insert running in background");
                long entryId = entryDao.insertEntry(entry);
                entry.setId(entryId);
                
                // Lên lịch đồng bộ
                scheduleSync();
                
                // Nếu đang online và có Firebase repository, đồng bộ ngay lập tức
                if (firestoreRepository != null && isOnline()) {
                    firestoreRepository.uploadEntry(entry)
                        .addOnSuccessListener(aVoid -> {
                            // Di chuyển thao tác cơ sở dữ liệu vào background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    entryDao.markAsSynced(entryId, entry.getFirebaseId(), System.currentTimeMillis());
                                    Log.d(TAG, "Entry synced immediately after insert: " + entryId);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error marking entry as synced after insert: " + e.getMessage());
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to sync entry after insert: " + entryId, e);
                        });
                }
                
                Log.d(TAG, "Entry inserted with ID: " + entryId + " for user ID: " + entry.userId);
            } catch (Exception e) {
                Log.e(TAG, "Insert failed", e);
            }
        });
    }

    public void updateDiary(Entry newEntry) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Cập nhật timestamp updatedAt
                newEntry.setUpdatedAt(System.currentTimeMillis());
                newEntry.setSynced(false);
                
                entryDao.updateEntry(newEntry);
                Log.d(TAG, "Updated entry with ID: " + newEntry.id);
                
                // Lên lịch đồng bộ
                scheduleSync();
                
                // Nếu đang online và có Firebase repository, đồng bộ ngay lập tức
                if (firestoreRepository != null && isOnline()) {
                    firestoreRepository.uploadEntry(newEntry)
                        .addOnSuccessListener(aVoid -> {
                            // Di chuyển thao tác cơ sở dữ liệu vào background thread
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    entryDao.markAsSynced(newEntry.id, newEntry.getFirebaseId(), System.currentTimeMillis());
                                    Log.d(TAG, "Entry synced immediately after update: " + newEntry.id);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error marking entry as synced after update: " + e.getMessage());
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to sync entry after update: " + newEntry.id, e);
                        });
                }
            } catch (Exception e) {
                Log.e(TAG, "Update failed", e);
            }
        });
    }
    
    public void deleteDiary(long entryId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Soft delete - đánh dấu là đã xóa
                entryDao.markAsDeleted(entryId, System.currentTimeMillis());
                Log.d(TAG, "Marked entry as deleted: " + entryId);
                
                // Lên lịch đồng bộ
                scheduleSync();
                
                // Nếu đang online và có Firebase repository, đồng bộ ngay lập tức
                if (firestoreRepository != null && isOnline()) {
                    Entry entry = entryDao.getEntryById(entryId);
                    if (entry != null) {
                        firestoreRepository.deleteEntry(entry)
                            .addOnSuccessListener(aVoid -> {
                                // Di chuyển thao tác cơ sở dữ liệu vào background thread
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    try {
                                        entryDao.markAsSynced(entryId, entry.getFirebaseId(), System.currentTimeMillis());
                                        Log.d(TAG, "Deletion synced immediately: " + entryId);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error marking entry as synced after deletion: " + e.getMessage());
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to sync deletion: " + entryId, e);
                            });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Delete failed", e);
            }
        });
    }
    
    private void scheduleSync() {
        if (firebaseUserId.isEmpty()) {
            Log.w(TAG, "Cannot schedule sync: No Firebase user ID available");
            return;
        }
        
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
                
        OneTimeWorkRequest syncWork = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES)
                .build();
                
        WorkManager.getInstance(context).enqueue(syncWork);
        Log.d(TAG, "Scheduled one-time sync work");
    }
    
    /**
     * Cài đặt đồng bộ định kỳ
     * Nên được gọi từ MainActivity hoặc Application
     */
    public void setupPeriodicSync() {
        if (firebaseUserId.isEmpty()) {
            Log.w(TAG, "Cannot setup periodic sync: No Firebase user ID available");
            return;
        }
        
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
                
        PeriodicWorkRequest syncWork = new PeriodicWorkRequest.Builder(
                SyncWorker.class, 
                15, TimeUnit.MINUTES)  // Đồng bộ mỗi 15 phút
                .setConstraints(constraints)
                .build();
                
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "diary_periodic_sync", 
                    ExistingPeriodicWorkPolicy.KEEP, 
                    syncWork);
        Log.d(TAG, "Scheduled periodic sync work (every 15 minutes)");
    }
    
    /**
     * Thực hiện đồng bộ thủ công theo yêu cầu người dùng
     */
    public void syncNow() {
        if (firebaseUserId.isEmpty()) {
            Log.w(TAG, "Cannot sync now: No Firebase user ID available");
            return;
        }
        
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
                
        OneTimeWorkRequest syncWork = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();
                
        WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "diary_manual_sync",
                    ExistingWorkPolicy.REPLACE,
                    syncWork);
        Log.d(TAG, "Manual sync requested");
    }
    
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
