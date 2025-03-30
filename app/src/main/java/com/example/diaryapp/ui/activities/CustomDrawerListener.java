package com.example.diaryapp.ui.activities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.diaryapp.interfaces.DrawerListenerCallback;

public class CustomDrawerListener implements DrawerLayout.DrawerListener {

    private Context context;

    public CustomDrawerListener(Context context) {
        this.context = context;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
