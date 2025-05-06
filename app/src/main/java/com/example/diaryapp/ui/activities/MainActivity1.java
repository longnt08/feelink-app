package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class MainActivity1 extends AppCompatActivity {
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check if user is already logged in
        if (isUserLoggedIn()) {
            // User is logged in, go to main activity
            Intent intent = new Intent(MainActivity1.this, MainActivity.class);
            startActivity(intent);
        } else {
            // User is not logged in, go to register activity
            Intent intent = new Intent(MainActivity1.this, RegisterActivity.class);
            startActivity(intent);
        }
        
        finish(); // Close MainActivity1 to prevent returning when pressing back
    }
    
    // Check if user is already logged in
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.contains(KEY_USER_ID);
    }
}