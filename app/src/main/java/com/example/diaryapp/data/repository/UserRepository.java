package com.example.diaryapp.data.repository;

import android.app.Application;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.UserDao;
import com.example.diaryapp.data.local.entities.User;

public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        DiaryDatabase db = DiaryDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void insertUser(User user) {
        new Thread(() -> userDao.insertUser(user)).start();
    }
}
