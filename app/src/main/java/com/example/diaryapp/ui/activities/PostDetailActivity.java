// PostDetailActivity.java
package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class PostDetailActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText postContent;
    private EditText commentField;
    private Button replyButton;
    private ImageView menuButton;
    private ImageView profileButton;
    private ImageView settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Initialize views
        usernameField = findViewById(R.id.usernameField);
        postContent = findViewById(R.id.postContent);
        commentField = findViewById(R.id.commentField);
        replyButton = findViewById(R.id.replyButton);
        menuButton = findViewById(R.id.menuButton);
        profileButton = findViewById(R.id.profileButton);
        settingsIcon = findViewById(R.id.settingsIcon);

        // Set up click listeners
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReply();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open menu or navigation drawer
                Toast.makeText(PostDetailActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to profile page
                Toast.makeText(PostDetailActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open settings
                Toast.makeText(PostDetailActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Load data if editing an existing post
        loadPostData();
    }

    private void loadPostData() {
        // This would typically fetch data from your database or intent extras
        // For now, we'll just set some placeholder text
        usernameField.setText("YourUsername");
    }

    private void submitReply() {
        String comment = commentField.getText().toString().trim();

        if (comment.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you would typically save the comment to your database
        Toast.makeText(this, "Reply submitted", Toast.LENGTH_SHORT).show();
        commentField.setText("");
    }
}