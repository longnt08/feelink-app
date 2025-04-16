package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.Settings;
import com.example.diaryapp.viewmodel.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {
    private Switch darkModeSwitch;
    private Spinner languageSpinner;
    private SettingsViewModel settingsViewModel;
    private int userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SettingsActivity", "onCreate called");
        setContentView(R.layout.activity_settings);

        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        languageSpinner = findViewById(R.id.spinner_language);
        
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new String[]{"English", "Tiếng Việt", "Italiano"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            
            settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

            Settings settings = settingsViewModel.getSettingsByUserId(userId);
            if(settings != null) {
                darkModeSwitch.setChecked(settings.darkMode);
                languageSpinner.setSelection(settings.language.equals("en") ? 0 : 1);
            }

            darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Settings newSettings = new Settings();
                newSettings.userId = userId;
                newSettings.darkMode = isChecked;
                newSettings.language = (languageSpinner.getSelectedItemPosition() == 0) ? "en" : "vi";
                settingsViewModel.updateSettings(newSettings);
            });
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error in onCreate: " + e.getMessage());
        }
    }
}
