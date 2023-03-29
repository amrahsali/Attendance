package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  {

    DrawerLayout drawer;
    NavigationView navigationView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                 switch (menuItem.getItemId()){

                     case R.id.add_staff:
                          Intent intent = new Intent(MainActivity.this, First_screen.class);
                     startActivity(intent);

                     case R.id.add_student:
                         Intent intent2 = new Intent(MainActivity.this, Add_studentActivity.class);
                         startActivity(intent2);
                     case R.id.navigation_record:
                         Intent intent3 = new Intent(MainActivity.this, View_recordActivity.class);
                         startActivity(intent3);
                     case R.id.navigation_exams:
                         Intent intent4 = new Intent(MainActivity.this, ExaminationActivity.class);
                         startActivity(intent4);
                     /*case R.id.navigation_courses:
                         Intent intent = new Intent(MainActivity.this, First_screen.class);
                         startActivity(intent);*/
                 }

                return true;
            }
        });




    }

}