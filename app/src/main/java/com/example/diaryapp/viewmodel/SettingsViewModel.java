package com.example.diaryapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.diaryapp.data.entities.Settings;
import com.example.diaryapp.repository.SettingsRepository;

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