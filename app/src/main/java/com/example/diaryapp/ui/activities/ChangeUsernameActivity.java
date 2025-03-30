package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class ChangeUsernameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        ImageView backButton = findViewById(R.id.backButton); // ID của nút back
        backButton.setOnClickListener(v -> finish()); // Đóng Activity hiện tại và quay lại
        EditText oldUsernameInput = findViewById(R.id.oldUsernameInput);
        EditText newUsernameInput = findViewById(R.id.newUsernameInput);
        Button confirmChangeUsernameButton = findViewById(R.id.confirmChangeUsernameButton);

        confirmChangeUsernameButton.setOnClickListener(v -> {
            String oldUsername = oldUsernameInput.getText().toString().trim();
            String newUsername = newUsernameInput.getText().toString().trim();

            if (oldUsername.isEmpty() || newUsername.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                // Simulate success and close activity
                Toast.makeText(this, "Username changed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}