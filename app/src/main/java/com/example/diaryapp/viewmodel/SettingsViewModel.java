package com.example.diaryapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.diaryapp.data.local.entities.Settings;
import com.example.diaryapp.data.repository.SettingsRepository;

public class SettingsViewModel extends AndroidViewModel {
    private SettingsRepository repository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        repository = new SettingsRepository(application);
    }

    public Settings getSettingsByUserId(int userId) {
        return repository.getSettingsByUserId(userId);
    }

    public void updateSettings(Settings settings) {
        repository.updateSettings(settings);
    }
}