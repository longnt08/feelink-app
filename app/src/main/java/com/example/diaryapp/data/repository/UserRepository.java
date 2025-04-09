package com.example.diaryapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.User;

public class UserRepository {
    private UserDao userDao;

    public UserRepository(Context context) {
        DiaryDatabase db = DiaryDatabase.getInstance(context);
        userDao = db.userDao();
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void insertNewUser(User user) {
        new Thread(() -> {
            long res = userDao.insertUser(user);
            Log.d("DEBUG", "User inserted with id " + res);
        }).start();
    }

}
