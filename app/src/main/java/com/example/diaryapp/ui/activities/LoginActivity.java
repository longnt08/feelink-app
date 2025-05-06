package com.example.diaryapp.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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
    private View rootView;
    private TextView tvSignup;
    private UserViewModel userViewModel;
    private ImageView loginBackground;
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USERNAME = "username";
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

        // Ánh xạ view
        rootView = findViewById(R.id.loginView);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        loginBackground = findViewById(R.id.loginBackground);

        // Khi người dùng nhấn vào EditText, ẩn background
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Nếu bàn phím hiện lên
                if (keypadHeight > screenHeight * 0.15) {
                    loginBackground.setVisibility(View.GONE);
                } else {
                    loginBackground.setVisibility(View.VISIBLE);
                }
            }
        });

        // Xử lý sự kiện đăng ký
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());
    }

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
                return userViewModel.getUserByEmail(email);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            progressDialog.dismiss();

            if (user != null && user.passwordHash.equals(password)) {
                saveUserSession(user);
                navigateToMainScreen();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserSession(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_USER_ID, user.id);
        editor.putString(KEY_USER_EMAIL, user.email);
        editor.putString(KEY_USERNAME, user.username);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.contains(KEY_USER_ID);
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
