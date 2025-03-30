package com.example.diaryapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView avatarImage;
    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);

        ImageView backButton = findViewById(R.id.backButton); // ID của nút back
        backButton.setOnClickListener(v -> {
            finish();
        });

        avatarImage = findViewById(R.id.avatarImage);
        usernameText = findViewById(R.id.usernameText);
        Button changeAvatarButton = findViewById(R.id.changeAvatarButton);
        Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set username from data (fake for demo purposes)
        usernameText.setText("YourUsername");

        // Change Avatar
        changeAvatarButton.setOnClickListener(v -> openImagePicker());

        // Change Username
        changeUsernameButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, ChangeUsernameActivity.class);
            startActivity(intent);
        });

        // Change Password
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Log Out
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(AccountSettingsActivity.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        });
    }

    // Open Image Picker for Avatar Change
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                avatarImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}