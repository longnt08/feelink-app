package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.diaryapp.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private Switch darkModeSwitch;
    private Spinner languageSpinner;
    private Button btnApplyChanges;
    private SeekBar seekBarFontSize;
    private int userId = 1;
    private String currentLanguage = "en";
    private boolean isDarkMode = false;
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_settings);

        // Đọc thiết lập hiện tại từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentLanguage = prefs.getString("language", "en");
        isDarkMode = prefs.getBoolean("darkMode", false);
        
        // Log thiết lập hiện tại
        Log.d(TAG, "Current settings from SharedPreferences - language: " + currentLanguage + ", darkMode: " + isDarkMode);
        
        // Thiết lập nút Back
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "btnBack not found");
        }
        
        // Khởi tạo views và xử lý sự kiện
        initializeViews();
    }
    
    private void initializeViews() {
        try {
            // Ánh xạ các thành phần giao diện
            darkModeSwitch = findViewById(R.id.switch_dark_mode);
            languageSpinner = findViewById(R.id.spinner_language);
            btnApplyChanges = findViewById(R.id.btn_apply_changes);
            seekBarFontSize = findViewById(R.id.seekbar_font_size);
            TextView tvFontSizeValue = findViewById(R.id.tv_font_size_value);
            
            if (darkModeSwitch != null) {
                darkModeSwitch.setChecked(isDarkMode);
                darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    isDarkMode = isChecked;
                    saveSettingsToSharedPreferences();
                });
            } else {
                Log.e(TAG, "darkModeSwitch not found");
            }
            
            if (languageSpinner != null) {
                setupLanguageSpinner();
            } else {
                Log.e(TAG, "languageSpinner not found");
            }
            
            if (btnApplyChanges != null) {
                btnApplyChanges.setOnClickListener(v -> applySettings());
            } else {
                Log.e(TAG, "btnApplyChanges not found");
            }
            
            // Cập nhật giá trị cỡ chữ khi kéo thanh trượt
            if (seekBarFontSize != null && tvFontSizeValue != null) {
                seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // Cập nhật giá trị hiển thị
                        tvFontSizeValue.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Không làm gì
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Không làm gì
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo giao diện", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupLanguageSpinner() {
        try {
            // Thiết lập adapter cho spinner ngôn ngữ
            String[] languages = {"English", "Tiếng Việt", "Italiano"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, 
                    android.R.layout.simple_spinner_item,
                    languages);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
            
            // Thiết lập vị trí ban đầu
            int languagePosition = 0;
            switch (currentLanguage) {
                case "en":
                    languagePosition = 0;
                    break;
                case "vi":
                    languagePosition = 1;
                    break;
                case "it":
                    languagePosition = 2;
                    break;
            }
            languageSpinner.setSelection(languagePosition);
            
            // Thiết lập listener
            languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String previousLanguage = currentLanguage;
                    switch (position) {
                        case 0:
                            currentLanguage = "en";
                            break;
                        case 1:
                            currentLanguage = "vi";
                            break;
                        case 2:
                            currentLanguage = "it";
                            break;
                        default:
                            currentLanguage = "en";
                    }
                    
                    if (!previousLanguage.equals(currentLanguage)) {
                        Log.d(TAG, "Language changed from " + previousLanguage + " to " + currentLanguage);
                        saveSettingsToSharedPreferences();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Không làm gì
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up language spinner: " + e.getMessage());
        }
    }
    
    // Phương thức lưu thiết lập vào SharedPreferences
    private void saveSettingsToSharedPreferences() {
        try {
            SharedPreferences.Editor editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit();
            editor.putString("language", currentLanguage);
            editor.putBoolean("darkMode", isDarkMode);
            editor.apply();
            Log.d(TAG, "Settings saved to SharedPreferences - language: " + currentLanguage + ", darkMode: " + isDarkMode);
        } catch (Exception e) {
            Log.e(TAG, "Error saving settings: " + e.getMessage());
        }
    }
    
    // Phương thức áp dụng thiết lập mới
    private void applySettings() {
        try {
            // Lưu thiết lập trước khi áp dụng
            saveSettingsToSharedPreferences();
            
            // Áp dụng chế độ sáng/tối
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            
            // Áp dụng ngôn ngữ mới
            Locale newLocale = new Locale(currentLanguage);
            Locale.setDefault(newLocale);
            
            Configuration config = new Configuration(getResources().getConfiguration());
            config.setLocale(newLocale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            
            // Thông báo và khởi động lại activity
            Toast.makeText(this, "Thiết lập đã được áp dụng", Toast.LENGTH_SHORT).show();
            recreate();
        } catch (Exception e) {
            Log.e(TAG, "Error applying settings: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi áp dụng thiết lập", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Phương thức khởi động lại MainActivity
    public void restartMainActivity() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error restarting MainActivity: " + e.getMessage());
        }
    }
}
