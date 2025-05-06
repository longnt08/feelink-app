package com.example.diaryapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.User;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public UserRepository(Context context) {
        DiaryDatabase db = DiaryDatabase.getInstance(context);
        userDao = db.userDao();
    }

    public User getUserByEmail(String email) {
        // For simplicity, we're keeping this on the main thread since it's used in login
        // In a production app, this should be moved off the main thread too
        return userDao.getUserByEmail(email);
    }

    // Insert user and return the ID synchronously 
    public long insertUserSync(User user) {
        final long[] userId = new long[1];
        final CountDownLatch latch = new CountDownLatch(1);
        
        executor.execute(() -> {
            try {
                userId[0] = userDao.insertUser(user);
                Log.d("DEBUG", "User inserted with id " + userId[0]);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            // Wait for the insertion to complete
            latch.await();
        } catch (InterruptedException e) {
            Log.e("ERROR", "User insertion interrupted", e);
        }
        
        return userId[0];
    }

    // Asynchronous version - use for non-critical operations
    public void insertNewUser(User user) {
        executor.execute(() -> {
            long res = userDao.insertUser(user);
            Log.d("DEBUG", "User inserted with id " + res);
        });
    }
}
