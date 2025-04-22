package com.example.diaryapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapp.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView splashLogo;
    private TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

//        splashLogo = findViewById(R.id.splash_logo);
        splashText = findViewById(R.id.splash_text);

        // Animation cho logo
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//        splashLogo.startAnimation(fadeIn);

        // Delay hiển thị text sau khi logo đã xuất hiện
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashText.setVisibility(View.VISIBLE);
                splashText.startAnimation(fadeIn);
            }
        }, 1500); // Hiển thị text sau 1.5 giây

        // Chuyển màn hình sau khi splash screen hoàn thành
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // Màn hình splash sẽ hiển thị trong 3 giây
    }
}
