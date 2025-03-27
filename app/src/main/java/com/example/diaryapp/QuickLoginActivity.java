package com.example.diaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class QuickLoginActivity extends AppCompatActivity {

    private EditText edtPassword;
    private Button btnLogin;

    // Mật khẩu cố định (có thể lưu SharedPreferences)
    private final String savedPassword = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_login);

        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String inputPassword = edtPassword.getText().toString();
            if (inputPassword.equals(savedPassword)) {
                // Mật khẩu đúng -> vào ứng dụng
                Intent intent = new Intent(QuickLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Mật khẩu sai
                Toast.makeText(QuickLoginActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
