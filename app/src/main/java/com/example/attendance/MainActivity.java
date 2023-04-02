package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    DrawerLayout layDL;
    NavigationView vNV;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layDL = findViewById(R.id.my_drawer_layout);
        vNV = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, layDL, toolbar, R.string.nav_open, R.string.nav_close);

        layDL.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setTitle("Home");

        // When we open the application first
        // time the fragment should be shown to the user
        // in this case it is home fragment
        AdminHomeFragment fragment = new AdminHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        if (savedInstanceState == null) {
            vNV.setCheckedItem(R.id.navigation_record);
        }
        NavClick();

    }
    private void NavClick() {
        vNV.setNavigationItemSelectedListener(item -> {
            Fragment frag = null;
            switch (item.getItemId()) {

                case R.id.navigation_record:
                    toolbar.setTitle("Records");
                    RecordFragment fragment = new RecordFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    break;
                case R.id.add_staff:
                    toolbar.setTitle("Add Staff");
                    Add_staff_Fragment fragment2 = new Add_staff_Fragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    break;
                case R.id.add_student:
                    toolbar.setTitle("Add Student");
                    Add_studentFragment fragment3 = new Add_studentFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "");
                    fragmentTransaction3.commit();
                    break;
                case R.id.navigation_exams:
                    toolbar.setTitle("Exams");
                    ExaminationFragment fragment4 = new ExaminationFragment();
                    FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.content, fragment4, "");
                    fragmentTransaction4.commit();
                    break;
                case R.id.navigation_courses:
                    toolbar.setTitle("Courses");
                    CoursesFragment fragment5 = new CoursesFragment();
                    FragmentTransaction fragmentTransaction5 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction5.replace(R.id.content, fragment5, "");
                    fragmentTransaction5.commit();
                    break;
                case R.id.home:
                    toolbar.setTitle("Home");
                    AdminHomeFragment fragment6 = new AdminHomeFragment();
                    FragmentTransaction fragmentTransaction6 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction6.replace(R.id.content, fragment6, "");
                    fragmentTransaction6.commit();
                    break;
            }
            layDL.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
       // Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.layFL);
        if (layDL.isDrawerOpen(GravityCompat.START)){
            layDL.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}