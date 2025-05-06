package com.example.diaryapp.ui.activities;

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

import com.example.diaryapp.R;

import java.io.IOException;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView avatarImage;
    private TextView usernameText;
    private String currentUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.account_setting);

            // Ánh xạ các view
            ImageView backButton = findViewById(R.id.backButton);
            avatarImage = findViewById(R.id.avatarImage);
            usernameText = findViewById(R.id.usernameText);
            Button changeAvatarButton = findViewById(R.id.changeAvatarButton);
            Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
            Button changePasswordButton = findViewById(R.id.changePasswordButton);
            Button logoutButton = findViewById(R.id.logoutButton);

            // Xử lý sự kiện nút quay lại
            backButton.setOnClickListener(v -> finish());

            // Set username từ dữ liệu (tạm thời hardcode)
            usernameText.setText("YourUsername");

            // Xử lý sự kiện thay đổi avatar
            changeAvatarButton.setOnClickListener(v -> openImagePicker());

            // Xử lý sự kiện thay đổi username
            changeUsernameButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AccountSettingsActivity.this, ChangeUsernameActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi mở trang thay đổi tên người dùng", Toast.LENGTH_SHORT).show();
                }
            });

            // Xử lý sự kiện thay đổi mật khẩu
            changePasswordButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AccountSettingsActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi mở trang thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });

            // Xử lý sự kiện đăng xuất
            logoutButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AccountSettingsActivity.this, LogoutActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi đăng xuất", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi khởi tạo trang cài đặt", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Mở image picker để chọn ảnh đại diện
    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi mở thư viện ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                avatarImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}