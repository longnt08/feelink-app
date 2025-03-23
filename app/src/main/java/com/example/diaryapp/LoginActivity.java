package com.example.diaryapp;

import android.content.Intent;
import android.content.res.Configuration;
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

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private TextView tvDiaryVertical, tvDiaryHorizontal;
    private View normalLayout, keyboardLayout;

    // Các view trong layout bàn phím
    private EditText etUsernameKeyboard, etPasswordKeyboard;
    private Button btnLoginKeyboard;
    private TextView tvSignupKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo các view
        initViews();

        // Thiết lập bộ lắng nghe sự kiện bàn phím
        setupKeyboardDetection();

        // Thiết lập đồng bộ dữ liệu
        setupDataSync();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        // Ánh xạ các thành phần giao diện chính
        normalLayout = findViewById(R.id.normalLayout);
        keyboardLayout = findViewById(R.id.keyboardLayout);

        // Views trong layout thường
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvDiaryVertical = findViewById(R.id.tvDiaryVertical);

        // Views trong layout bàn phím (có thể null nếu đang ở layout dọc)
        tvDiaryHorizontal = findViewById(R.id.tvDiaryHorizontal);

        // Kiểm tra xem có đang ở layout ngang không
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            etUsernameKeyboard = findViewById(R.id.etUsernameKeyboard);
            etPasswordKeyboard = findViewById(R.id.etPasswordKeyboard);
            btnLoginKeyboard = findViewById(R.id.btnLoginKeyboard);
            tvSignupKeyboard = findViewById(R.id.tvSignupKeyboard);
        }
    }

    private void setupDataSync() {
        // Chỉ thiết lập đồng bộ dữ liệu khi ở chế độ ngang
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && etUsernameKeyboard != null && etPasswordKeyboard != null) {

            // Đồng bộ username
            etUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etUsernameKeyboard != null && !s.toString().equals(etUsernameKeyboard.getText().toString())) {
                        etUsernameKeyboard.setText(s);
                    }
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

            // Đồng bộ password
            etPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (etPasswordKeyboard != null && !s.toString().equals(etPasswordKeyboard.getText().toString())) {
                        etPasswordKeyboard.setText(s);
                    }
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

    private void setupClickListeners() {
        // Xử lý sự kiện đăng ký
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý đăng nhập ở đây
            }
        });

        // Thiết lập click listeners cho layout bàn phím nếu đang ở chế độ ngang
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (tvSignupKeyboard != null) {
                tvSignupKeyboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });
            }

            if (btnLoginKeyboard != null) {
                btnLoginKeyboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý đăng nhập ở đây
                    }
                });
            }
        }
    }

    private void setupKeyboardDetection() {
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
                    if (normalLayout != null) normalLayout.setVisibility(View.GONE);
                    if (keyboardLayout != null) keyboardLayout.setVisibility(View.VISIBLE);
                } else {
                    // Bàn phím ẩn đi
                    if (normalLayout != null) normalLayout.setVisibility(View.VISIBLE);
                    if (keyboardLayout != null) keyboardLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Khởi tạo lại các view
        initViews();

        // Thiết lập lại bộ lắng nghe sự kiện bàn phím
        setupKeyboardDetection();

        // Thiết lập lại đồng bộ dữ liệu
        setupDataSync();

        // Thiết lập lại sự kiện click
        setupClickListeners();
    }
}