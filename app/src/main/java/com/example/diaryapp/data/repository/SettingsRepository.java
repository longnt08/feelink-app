package com.example.diaryapp.data.repository;

import android.app.Application;

import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.dao.SettingsDao;
import com.example.diaryapp.data.local.entities.Settings;

public class SettingsRepository {
    private SettingsDao settingsDao;

    public SettingsRepository(Application application) {
        DiaryDatabase db = DiaryDatabase.getInstance(application);
        settingsDao = db.settingsDao();
    }

    public Settings getSettingsByUserId(int userId) {
        return settingsDao.getSettingsByUserId(userId);
    }

    public void updateSettings(Settings settings) {
        new Thread(() -> settingsDao.insertOrUpdate(settings)).start();
    }
}
