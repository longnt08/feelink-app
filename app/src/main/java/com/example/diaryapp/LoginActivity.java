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

    // Lưu trạng thái
    private String savedUsername = "";
    private String savedPassword = "";

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
        try {
            // Ánh xạ các thành phần giao diện chính
            normalLayout = findViewById(R.id.normalLayout);
            keyboardLayout = findViewById(R.id.keyboardLayout);

            // Views trong layout thường
            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvSignup = findViewById(R.id.tvSignup);
            tvDiaryVertical = findViewById(R.id.tvDiaryVertical);

            // Views trong layout bàn phím
            tvDiaryHorizontal = findViewById(R.id.tvDiaryHorizontal);

            // Khôi phục dữ liệu đã lưu
            if (!savedUsername.isEmpty() && etUsername != null) {
                etUsername.setText(savedUsername);
            }

            if (!savedPassword.isEmpty() && etPassword != null) {
                etPassword.setText(savedPassword);
            }

            // Kiểm tra xem có đang ở layout ngang không
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                etUsernameKeyboard = findViewById(R.id.etUsernameKeyboard);
                etPasswordKeyboard = findViewById(R.id.etPasswordKeyboard);
                btnLoginKeyboard = findViewById(R.id.btnLoginKeyboard);
                tvSignupKeyboard = findViewById(R.id.tvSignupKeyboard);

                // Khôi phục dữ liệu cho layout bàn phím
                if (!savedUsername.isEmpty() && etUsernameKeyboard != null) {
                    etUsernameKeyboard.setText(savedUsername);
                }

                if (!savedPassword.isEmpty() && etPasswordKeyboard != null) {
                    etPasswordKeyboard.setText(savedPassword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDataSync() {
        try {
            // Lưu dữ liệu khi người dùng nhập
            if (etUsername != null) {
                etUsername.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        savedUsername = s.toString();
                        if (etUsernameKeyboard != null && !s.toString().equals(etUsernameKeyboard.getText().toString())) {
                            etUsernameKeyboard.setText(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }

            if (etPassword != null) {
                etPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        savedPassword = s.toString();
                        if (etPasswordKeyboard != null && !s.toString().equals(etPasswordKeyboard.getText().toString())) {
                            etPasswordKeyboard.setText(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }

            // Chỉ thiết lập đồng bộ dữ liệu khi ở chế độ ngang
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && etUsernameKeyboard != null && etPasswordKeyboard != null) {

                etUsernameKeyboard.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        savedUsername = s.toString();
                        if (etUsername != null && !s.toString().equals(etUsername.getText().toString())) {
                            etUsername.setText(s);
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
                        savedPassword = s.toString();
                        if (etPassword != null && !s.toString().equals(etPassword.getText().toString())) {
                            etPassword.setText(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        try {
            // Xử lý sự kiện đăng ký
            if (tvSignup != null) {
                tvSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });
            }

            // Xử lý sự kiện đăng nhập
            if (btnLogin != null) {
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý đăng nhập ở đây
                    }
                });
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupKeyboardDetection() {
        try {
            if (normalLayout == null || keyboardLayout == null) {
                return;
            }

            final View rootView = findViewById(android.R.id.content);
            if (rootView == null) {
                return;
            }

            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            // Lưu trạng thái hiện tại
            if (etUsername != null) {
                savedUsername = etUsername.getText().toString();
            } else if (etUsernameKeyboard != null) {
                savedUsername = etUsernameKeyboard.getText().toString();
            }

            if (etPassword != null) {
                savedPassword = etPassword.getText().toString();
            } else if (etPasswordKeyboard != null) {
                savedPassword = etPasswordKeyboard.getText().toString();
            }

            // Tải lại layout
            setContentView(R.layout.activity_login);

            // Khởi tạo lại các view
            initViews();

            // Thiết lập lại bộ lắng nghe sự kiện bàn phím
            setupKeyboardDetection();

            // Thiết lập lại đồng bộ dữ liệu
            setupDataSync();

            // Thiết lập lại sự kiện click
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái
        outState.putString("username", savedUsername);
        outState.putString("password", savedPassword);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Khôi phục trạng thái
        if (savedInstanceState != null) {
            savedUsername = savedInstanceState.getString("username", "");
            savedPassword = savedInstanceState.getString("password", "");
        }
    }
}