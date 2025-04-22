package com.example.diaryapp.ui.activities;


import android.content.Intent;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.widget.TextView;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.repository.EntryRepository;
import com.example.diaryapp.ui.adapters.DiaryAdapter;
import com.example.diaryapp.viewmodel.DiaryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DiaryDatabase diaryDatabase;
    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Entry> entries = new ArrayList<>();
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabAddDiary, fabAccount;
    private static final String PREF_NAME = "DiaryAppPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIREBASE_USER_ID = "firebase_user_id";
    private long currentUserId = -1;
    private String currentUsername = "";
    private String firebaseUserId = "";
    private DiaryViewModel diaryViewModel;
    TextView drawerUsername;
    private EntryRepository entryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // setup layout
        setupLayout();

        // Initialize database and load entries in background
        initializeDatabase();

        // Khởi tạo EntryRepository
        entryRepository = new EntryRepository(this);

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
            if (swipeRefreshLayout != null && !isLoading) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Connect ViewModel to Adapter for delete functionality
        diaryAdapter.setViewModel(diaryViewModel);

        // goi load du lieu
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    diaryViewModel.loadEntries(currentUserId);
        }, 200);

        // nut 3 gach mo menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Xử lý sự kiện khi chọn item cài đặt trong menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.d("MainActivity", "Menu item clicked: " + id);
            if (id == R.id.nav_settings) {
                // Chuyển sang trang SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_sync) {
                // Thực hiện đồng bộ thủ công khi người dùng chọn
                performManualSync();
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

        // Thiết lập SwipeRefreshLayout để đồng bộ khi người dùng kéo xuống
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::performManualSync);
        }

        // Check and request permissions
        checkAndRequestPermissions();

        // Thiết lập đồng bộ định kỳ
        setupPeriodicSync();
    }

    private void setupLayout() {
        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentUserId = sharedPreferences.getLong(KEY_USER_ID, -1);
        currentUsername = sharedPreferences.getString(KEY_USERNAME, "");
        firebaseUserId = sharedPreferences.getString(KEY_FIREBASE_USER_ID, "");

        // Nếu chưa có firebase_user_id, lấy từ FirebaseAuth nếu có
        if (firebaseUserId.isEmpty()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                firebaseUserId = firebaseUser.getUid();
                // Lưu lại Firebase user ID
                sharedPreferences.edit().putString(KEY_FIREBASE_USER_ID, firebaseUserId).apply();
            }
        }

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
        drawerUsername = findViewById(R.id.drawerUsername);

        // Thêm SwipeRefreshLayout nếu có trong layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // setup toolbar thanh ActionBar
        setSupportActionBar(toolbar);
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

    /**
     * Thiết lập đồng bộ định kỳ với Firebase
     */
    private void setupPeriodicSync() {
        if (firebaseUserId.isEmpty()) {
            Log.w("MainActivity", "Cannot setup sync: No Firebase user ID available");
            return;
        }

        EntryRepository entryRepository = new EntryRepository(this);
        entryRepository.setupPeriodicSync();
        Log.d("MainActivity", "Periodic sync setup completed");
    }

    /**
     * Thực hiện đồng bộ thủ công khi người dùng yêu cầu
     */
    private void performManualSync() {
        if (firebaseUserId.isEmpty()) {
            Toast.makeText(this, "Không thể đồng bộ: Bạn chưa đăng nhập Firebase", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        Toast.makeText(this, "Đang đồng bộ dữ liệu...", Toast.LENGTH_SHORT).show();
        EntryRepository repo = new EntryRepository(this);
        repo.syncNow();

        // Sau 2 giây, tải lại dữ liệu từ cơ sở dữ liệu cục bộ
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            diaryViewModel.loadEntries(currentUserId);
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(this, "Đồng bộ hoàn tất", Toast.LENGTH_SHORT).show();
        }, 2000);
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        diaryViewModel.loadEntries(currentUserId);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        diaryViewModel.loadEntries(currentUserId);
    }
}