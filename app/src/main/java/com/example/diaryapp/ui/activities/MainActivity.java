package com.example.diaryapp.ui.activities;


import android.content.Intent;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Bundle;
import android.view.Gravity;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.ui.adapters.DiaryAdapter;
import com.example.diaryapp.viewmodel.DiaryViewModel;
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
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private long currentUserId = -1;
    private String currentUsername = "";
    private DiaryViewModel diaryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // setup layout
        setupLayout();

        // Initialize database and load entries in background
        initializeDatabase();

        // viewmodel + livedata setup
        diaryViewModel = new ViewModelProvider(this).get(DiaryViewModel.class);
        diaryViewModel.init(this);

        diaryViewModel.getEntries().observe(this, entries -> {
                    if (entries != null) {
                        Log.d("MainActivity", "Observed entries: " + entries.size());
                        diaryAdapter.setData(entries);
                    }
        });
        diaryViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // goi load du lieu
        diaryViewModel.loadEntries(currentUserId);

        // nut 3 gach mo menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Xử lý sự kiện khi chọn item cài đặt trong menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.d("MainActivity", "Settings menu item clicked");
            if (id == R.id.nav_settings) {
                // Chuyển sang trang SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // su kien an nut them bai viet
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryEntryActivity.class);
                // Pass the user ID to the activity
                intent.putExtra("userId", currentUserId);
                intent.putExtra(AddDiaryEntryActivity.MODE_KEY, AddDiaryEntryActivity.MODE_CREATE);
                startActivity(intent);
            }
        });

        // Account button click event
        fabAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", currentUserId);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        // Check and request permissions
        checkAndRequestPermissions();
    }

    private void setupLayout() {
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong(KEY_USER_ID, -1);
        currentUsername = sharedPreferences.getString(KEY_USERNAME, "");

        if (currentUserId == -1) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // anh xa view
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        fabAccount = findViewById(R.id.fabAccount);
        fabAddDiary = findViewById(R.id.fabAddDiary);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycleView);
        NavigationView navigationView = findViewById(R.id.navigationView);
        progressBar.setVisibility(View.GONE);

        // setup toolbar thanh ActionBar
        setSupportActionBar(toolbar);
        // setup adapter
        diaryAdapter = new DiaryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(diaryAdapter);
    }

    private void initializeDatabase() {
        // Show progress indicator
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Initialize database without allowing main thread queries
        diaryDatabase = DiaryDatabase.getInstance(getApplicationContext());
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
        diaryViewModel.loadEntries(currentUserId);
    }
}