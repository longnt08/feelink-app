package com.example.diaryapp.repository;

import android.app.Application;

import com.example.diaryapp.data.JournalDatabase;
import com.example.diaryapp.data.dao.SettingsDao;
import com.example.diaryapp.data.entities.Settings;

public class SettingsRepository {
    private SettingsDao settingsDao;

    public SettingsRepository(Application application) {
        JournalDatabase db = JournalDatabase.getInstance(application);
        settingsDao = db.settingsDao();
    }

    public Settings getSettingsByUserId(int userId) {
        return settingsDao.getSettingsByUserId(userId);
    }

    public void updateSettings(Settings settings) {
        new Thread(() -> settingsDao.insertOrUpdate(settings)).start();
    }
}
