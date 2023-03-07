package com.nycschools.ssharar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
/* Author: Steven Sharar
   Date: 3/3/2023
 */
public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        lottieAnimationView = findViewById(R.id.lottie);
        logo.animate().translationY(-2000).setDuration(500).setStartDelay(2000);
        lottieAnimationView.animate().translationY(-2000).setDuration(500).setStartDelay(2000);

        int SPLASH_TIME_OUT = 2000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
