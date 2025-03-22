package com.example.diaryapp.repository;

import android.app.Application;

import com.example.diaryapp.data.JournalDatabase;
import com.example.diaryapp.data.dao.UserDao;
import com.example.diaryapp.data.entities.User;

public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        JournalDatabase db = JournalDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void insertUser(User user) {
        new Thread(() -> userDao.insertUser(user)).start();
    }
}
