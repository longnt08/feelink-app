package com.example.diaryapp.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.diaryapp.R;
import com.example.diaryapp.data.DiaryDatabase;
import com.example.diaryapp.data.local.entities.Entry;
import com.example.diaryapp.data.local.entities.User;
import com.example.diaryapp.interfaces.DrawerListenerCallback;
import com.example.diaryapp.ui.adapters.DiaryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DiaryDatabase diaryDatabase;
    RecyclerView recyclerView;
    DiaryAdapter diaryAdapter;
    List<Entry> entries;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabAddDiary, fabAccount;
    private Animation rotateOpen, rotateClose;

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

        drawerLayout.addDrawerListener(new CustomDrawerListener( this));

        // bam nut them
        fabAddDiary.setOnClickListener(v -> {
            v.startAnimation(rotateOpen);
            Toast.makeText(this, "Add new entry", Toast.LENGTH_SHORT).show();
        });

        // bam nut tai khoan
        fabAccount.setOnClickListener(v -> {
            v.startAnimation(rotateOpen);
            Toast.makeText(this, "Your account", Toast.LENGTH_SHORT).show();
        });




        diaryDatabase = Room.databaseBuilder(getApplicationContext(),
                DiaryDatabase.class, "diary_db")
                .allowMainThreadQueries()
                .build();
        User user = new User();
        user.username = "Goku";
        user.email = "goku@gmail.com";
        user.passwordHash = "12345678";
        user.createdAt = System.currentTimeMillis();
        long res = diaryDatabase.userDao().insertUser(user);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       entries = new ArrayList<>();
       entries.add(new Entry(3, "23/3/2025", "this is the demo 0, the content will be set to be very long to test the result when display in screen, hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", System.currentTimeMillis(), System.currentTimeMillis()));
       entries.add(new Entry(3, "23/3/2023", "this is the demo 1", System.currentTimeMillis(), System.currentTimeMillis()));
       entries.add(new Entry(3, "26/3/2025", "this is the demo 2", System.currentTimeMillis(), System.currentTimeMillis()));
       entries.add(new Entry(3, "12/3/2025", "this is the demo 3", System.currentTimeMillis(), System.currentTimeMillis()));
       entries.add(new Entry(3, "8/3/2025", "this is the demo 4", System.currentTimeMillis(), System.currentTimeMillis()));
       entries.add(new Entry(3, "4/3/2025", "this is the demo 5", System.currentTimeMillis(), System.currentTimeMillis()));

       diaryAdapter = new DiaryAdapter(this, entries);
       recyclerView.setAdapter(diaryAdapter);

    }

    // bam back thoat drawer
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}