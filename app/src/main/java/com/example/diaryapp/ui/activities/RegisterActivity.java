package com.example.diaryapp.ui.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diaryapp.R;
import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private EditText etUsername, etPassword, etEmail;
    private Button btnSignup;
    private TextView tvLogin;
    private UserViewModel userViewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        
        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        
        // Add click listener for the login text
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to login screen
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic email validation
        if(!isValidEmail(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic password validation
        if(password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();
        // Create Firebase user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        // Save user profile to Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", username);
                        userMap.put("email", email);
                        userMap.put("created_at", System.currentTimeMillis());
                        if (fbUser != null) {
                            mFirestore.collection("users").document(fbUser.getUid())
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    // on Firestore success, save locally
                                    User newUser = new User(username, email, password, System.currentTimeMillis());
                                    new RegisterUserTask(newUser).execute();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this,
                                        "Firestore error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,
                            "Firebase error: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    
    // AsyncTask to handle registration in the background
    private class RegisterUserTask extends AsyncTask<Void, Void, Long> {
        private User user;
        
        RegisterUserTask(User user) {
            this.user = user;
        }
        
        @Override
        protected Long doInBackground(Void... voids) {
            // Use the synchronous insert method
            return userViewModel.insertUserSync(user);
        }
        
        @Override
        protected void onPostExecute(Long userId) {
            // Dismiss progress dialog
            progressDialog.dismiss();
            
            if (userId > 0) {
                // Registration successful
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                SharedPreferences sharedPreferences = getSharedPreferences("DiaryAppPrefs", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putLong("user_id", userId);
//                editor.apply();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Registration failed
                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
