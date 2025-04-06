package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Xóa dữ liệu đăng nhập đã lưu (nếu có sử dụng SharedPreferences)
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // hoặc editor.remove("token") nếu chỉ muốn xóa token
        editor.apply();

        // Hiển thị thông báo
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

        // Chuyển về LoginActivity và xóa history để không back được
        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng LogoutActivity
    }
}
