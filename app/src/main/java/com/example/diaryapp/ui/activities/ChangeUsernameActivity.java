package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diaryapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangeUsernameActivity extends AppCompatActivity {
    private TextView currentUsernameText;
    private TextInputEditText newUsernameInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        // Initialize views
        currentUsernameText = findViewById(R.id.currentUsernameText);
        newUsernameInput = findViewById(R.id.newUsernameInput);
        saveButton = findViewById(R.id.saveButton);

        // Set up back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Set up save button click listener
        saveButton.setOnClickListener(v -> {
            String newUsername = newUsernameInput.getText().toString().trim();

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên người dùng mới", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Implement username change logic here
            Toast.makeText(this, "Đổi tên người dùng thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}