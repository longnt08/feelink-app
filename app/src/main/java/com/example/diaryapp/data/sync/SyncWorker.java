package com.example.diaryapp.data.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker";
    private static final String PREF_NAME = "DiaryAppPrefs";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Sync worker starting...");
        
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long localUserId = prefs.getLong("user_id", -1);
        String firebaseUserId = prefs.getString("firebase_user_id", "");
        
        if (localUserId == -1 || firebaseUserId.isEmpty()) {
            Log.e(TAG, "Invalid user IDs for sync: localUserId=" + localUserId + ", firebaseUserId=" + firebaseUserId);
            return Result.failure();
        }
        
        Log.d(TAG, "Starting sync for user: local=" + localUserId + ", firebase=" + firebaseUserId);
        SyncManager syncManager = new SyncManager(getApplicationContext(), localUserId, firebaseUserId);
        
        try {
            // Thực hiện đồng bộ và đợi kết quả
            SyncResult result = Tasks.await(syncManager.performSync());
            
            if (result.success) {
                Log.d(TAG, "Sync completed successfully. Uploaded: " + result.localUploaded + 
                        ", Downloaded: " + result.remoteDownloaded);
                return Result.success();
            } else {
                Log.e(TAG, "Sync failed with error: " + (result.error != null ? result.error.getMessage() : "unknown"));
                return Result.retry();
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Sync failed with exception", e);
            return Result.retry();
        }
    }
}