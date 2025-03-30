package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Chuyển sang màn hình đăng nhập
        Intent intent = new Intent(MainActivity1.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng MainActivity để không quay lại khi nhấn "Back"
    }
}