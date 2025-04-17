package com.example.diaryapp.ui.activities;


import android.content.Intent;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.ui.adapters.DiaryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DiaryDatabase diaryDatabase;
    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Entry> entries = new ArrayList<>();
    private ProgressBar progressBar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabAddDiary, fabAccount;
    private Animation rotateOpen, rotateClose;
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // anh xa view
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        fabAccount = findViewById(R.id.fabAccount);
        fabAddDiary = findViewById(R.id.fabAddDiary);
        
        // Create a ProgressBar programmatically since it's not in the layout
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        
        // Add to layout
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(
                DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        drawerLayout.addView(progressBar, params);

        // animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);

        // setup toobar thanh ActionBar
        setSupportActionBar(toolbar);

        // nut 3 gach mo menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        
        if (currentUserId == -1) {
            // Not logged in, redirect to RegisterActivity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize adapter with empty list (will be populated in background)
        diaryAdapter = new DiaryAdapter(this, entries);
        recyclerView.setAdapter(diaryAdapter);
        
        // Initialize database and load entries in background
        initializeDatabase();

        // su kien an nut them bai viet
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryEntryActivity.class);
                // Pass the user ID to the activity
                intent.putExtra("userId", currentUserId);
                startActivity(intent);
            }
        });
        
        // Account button click event
        fabAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        // Check and request permissions
        checkAndRequestPermissions();
    }
    
    private void initializeDatabase() {
        // Show progress indicator
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        // Initialize database without allowing main thread queries
        diaryDatabase = DiaryDatabase.getInstance(getApplicationContext());
        
        // Load entries in background
        new LoadEntriesTask().execute(currentUserId);
    }
    
    private class LoadEntriesTask extends AsyncTask<Integer, Void, List<Entry>> {
        @Override
        protected List<Entry> doInBackground(Integer... params) {
            int userId = params[0];
            try {
                // Try to fetch entries for this user
                // return diaryDatabase.entryDao().getEntriesByUserId(userId);
                
                // For now, just create some sample entries
                List<Entry> tempEntries = new ArrayList<>();
                tempEntries.add(new Entry(userId, "Vui qua", "this is the demo 0, the content will be set to be very long to test the result when display in screen, hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww","happy", System.currentTimeMillis()));
                tempEntries.add(new Entry(userId, "Con ga trong", "this is the demo 1", "normal", System.currentTimeMillis()));
                tempEntries.add(new Entry(userId, "Viet Nam vs China", "this is the demo 2", "thinking", System.currentTimeMillis()));
                tempEntries.add(new Entry(userId, "Suy nghi", "this is the demo 3", "thinking", System.currentTimeMillis()));
                tempEntries.add(new Entry(userId, "Haizz", "this is the demo 4", "sad", System.currentTimeMillis()));
                tempEntries.add(new Entry(userId, "Da qua pepsi oi", "this is the demo 5", "happy", System.currentTimeMillis()));
                
                return tempEntries;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        
        @Override
        protected void onPostExecute(List<Entry> result) {
            // Hide progress indicator
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            
            // Update UI with the loaded entries
            entries.clear();
            if (result != null && !result.isEmpty()) {
                entries.addAll(result);
            }
            
            diaryAdapter.notifyDataSetChanged();
        }
    }
    
    private void checkAndRequestPermissions() {
        //Cấp quyền cho BroadcastReceiver trong lớp ReminderReceiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh entries when returning to this screen
        if (diaryDatabase != null && currentUserId != -1) {
            new LoadEntriesTask().execute(currentUserId);
        }
    }
}