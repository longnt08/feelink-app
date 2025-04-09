package com.example.diaryapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvDiary, tvDiaryHorizontal;
    private View normalLayout, keyboardLayout;
    private UserViewModel userViewModel;
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainScreen();
            return;
        }

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

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
                loginUser();
            }
        });

        // Đảm bảo nút đăng nhập trong layout bàn phím cũng hoạt động
        Button btnLoginKeyboard = keyboardLayout.findViewById(R.id.btnLogin);
        if (btnLoginKeyboard != null) {
            btnLoginKeyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUser();
                }
            });
        }
    }

    // Phương thức để đăng nhập người dùng
    private void loginUser() {
        final String email = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();
        
        // Use AsyncTask to perform login in background
        new LoginUserTask(email, password).execute();
    }
    
    // AsyncTask to handle login in the background
    private class LoginUserTask extends AsyncTask<Void, Void, User> {
        private String email;
        private String password;
        
        LoginUserTask(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        @Override
        protected User doInBackground(Void... voids) {
            try {
                // Get user from database on background thread
                return userViewModel.getUserByEmail(email);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(User user) {
            // Dismiss progress dialog
            progressDialog.dismiss();
            
            if (user != null && user.passwordHash.equals(password)) {
                // Login successful
                saveUserSession(user);
                navigateToMainScreen();
            } else {
                // Login failed
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save user session data to SharedPreferences
    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, user.id);
        editor.putString(KEY_USER_EMAIL, user.email);
        editor.apply();
    }

    // Check if user is already logged in
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.contains(KEY_USER_ID);
    }

    // Navigate to main screen
    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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