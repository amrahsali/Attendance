package com.example.attendance.LoginModule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.example.attendance.R;

public class Splash_screenActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }



        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(Splash_screenActivity.this, Login.class);
            Splash_screenActivity.this.startActivity(intent);
            this.finish();
        }, 1000);

    }@Override
    public void onBackPressed() {
        // Disable the back button functionality in the splash screen activity
        moveTaskToBack(true);
    }

}