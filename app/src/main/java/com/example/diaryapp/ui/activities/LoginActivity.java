package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvDiary, tvDiaryHorizontal;
    private View normalLayout, keyboardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần giao diện
        normalLayout = findViewById(R.id.normalLayout);
        keyboardLayout = findViewById(R.id.keyboardLayout);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvDiary = findViewById(R.id.tvDiaryVertical);
        tvDiaryHorizontal = findViewById(R.id.tvDiaryHorizontal);

        // Thiết lập bộ lắng nghe sự kiện bàn phím
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();

                // Tính toán chiều cao của bàn phím
                int keypadHeight = screenHeight - r.bottom;

                // Nếu chiều cao của bàn phím lớn hơn 15% chiều cao màn hình,
                // coi như bàn phím đang hiển thị
                if (keypadHeight > screenHeight * 0.15) {
                    // Bàn phím hiện lên
                    normalLayout.setVisibility(View.GONE);
                    keyboardLayout.setVisibility(View.VISIBLE);
                } else {
                    // Bàn phím ẩn đi
                    normalLayout.setVisibility(View.VISIBLE);
                    keyboardLayout.setVisibility(View.GONE);
                }
            }
        });

        // Đồng bộ dữ liệu giữa hai layout
        setupTextSynchronization();

        // Xử lý sự kiện đăng ký
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Đảm bảo nút đăng ký trong layout bàn phím cũng hoạt động
        TextView tvSignupKeyboard = keyboardLayout.findViewById(R.id.tvSignup);
        if (tvSignupKeyboard != null) {
            tvSignupKeyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý đăng nhập ở đây
            }
        });

        // Đảm bảo nút đăng nhập trong layout bàn phím cũng hoạt động
        Button btnLoginKeyboard = keyboardLayout.findViewById(R.id.btnLogin);
        if (btnLoginKeyboard != null) {
            btnLoginKeyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý đăng nhập ở đây
                }
            });
        }
    }

    // Phương thức để đồng bộ dữ liệu giữa hai layout
    private void setupTextSynchronization() {
        // Đồng bộ username
        final EditText etUsernameKeyboard = keyboardLayout.findViewById(R.id.etUsername);
        if (etUsernameKeyboard != null) {
            etUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    etUsernameKeyboard.setText(s);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            etUsernameKeyboard.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(etUsername.getText().toString())) {
                        etUsername.setText(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Đồng bộ password
        final EditText etPasswordKeyboard = keyboardLayout.findViewById(R.id.etPassword);
        if (etPasswordKeyboard != null) {
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    etPasswordKeyboard.setText(s);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            etPasswordKeyboard.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(etPassword.getText().toString())) {
                        etPassword.setText(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
}