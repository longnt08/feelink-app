package com.example.diaryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ImageView backButton = findViewById(R.id.backButton); // ID của nút back
        backButton.setOnClickListener(v -> finish()); // Đóng Activity hiện tại và quay lại
        EditText oldPasswordInput = findViewById(R.id.oldPasswordInput);
        EditText newPasswordInput = findViewById(R.id.newPasswordInput);
        Button confirmChangePasswordButton = findViewById(R.id.confirmChangePasswordButton);

        confirmChangePasswordButton.setOnClickListener(v -> {
            String oldPassword = oldPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate success and close activity
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}