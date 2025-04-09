package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

        // bat su kien chon menu
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.
//                }
//            }
//        });

        // bam nut them



        diaryDatabase = Room.databaseBuilder(getApplicationContext(),
                DiaryDatabase.class, "diary_db")
                .allowMainThreadQueries()
                .build();

        User user = new User("Goku", "goku@gmail.com", "12345678", System.currentTimeMillis());

        long res = diaryDatabase.userDao().insertUser(user);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       entries = new ArrayList<>();
       entries.add(new Entry(3, "Vui qua", "this is the demo 0, the content will be set to be very long to test the result when display in screen, hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww","happy", System.currentTimeMillis()));
       entries.add(new Entry(3, "Con ga trong", "this is the demo 1", "normal", System.currentTimeMillis()));
       entries.add(new Entry(3, "Viet Nam vs China", "this is the demo 2", "thinking", System.currentTimeMillis()));
       entries.add(new Entry(3, "Suy nghi", "this is the demo 3", "thinking", System.currentTimeMillis()));
       entries.add(new Entry(3, "Haizz", "this is the demo 4", "sad", System.currentTimeMillis()));
       entries.add(new Entry(3, "Da qua pepsi oi", "this is the demo 5", "happy", System.currentTimeMillis()));

       diaryAdapter = new DiaryAdapter(this, entries);
       recyclerView.setAdapter(diaryAdapter);

       // su kien an nut them bai viet
        fabAddDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryEntryActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}