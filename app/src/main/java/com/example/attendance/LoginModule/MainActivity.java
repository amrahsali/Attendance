package com.example.attendance.LoginModule;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.AttendanceModule.RecordFragment;
import com.example.attendance.ExamsModule.ExaminationFragment;
import com.example.attendance.FacultyModule.CoursesFragment;
import com.example.attendance.FacultyModule.FacultyFragment;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffListFragment;
import com.example.attendance.StudentModule.Add_studentFragment;
import com.example.attendance.Utility.FacultyBottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FacultyBottomSheetDialogFragment.FacultyDialogListener {

    DrawerLayout layDL;
    NavigationView vNV;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    TextView welcomeMessage;
    ImageView profileImage;


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
        mAuth = FirebaseAuth.getInstance();
        // Customize the toolbar with profile image and welcome message
        profileImage = findViewById(R.id.profile_img);
        welcomeMessage = findViewById(R.id.welcome_msg);
        welcomeMessage.setText("Welcome, Admin!");

        // When we open the application first time, the fragment should be shown to the user
        // in this case, it is the home fragment
        AdminHomeFragment fragment = new AdminHomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();

        if (savedInstanceState == null) {
            vNV.setCheckedItem(R.id.navigation_record);
        }
        NavClick();

        Button logoutButton = findViewById(R.id.nav_bt);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,  "Logout ", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent i = new Intent(MainActivity.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                   finish();
            }
        });
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
                    StaffListFragment fragment2 = new StaffListFragment();
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
                case R.id.navigation_faculty:
                    toolbar.setTitle("Faculty");
                    FacultyFragment fragment7 = new FacultyFragment();
                    FragmentTransaction fragmentTransaction7 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction7.replace(R.id.content, fragment7, "");
                    fragmentTransaction7.commit();
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

    @Override
    public void onSaveClicked(String facultyName, ArrayList<String> departmentNames) {

    }


}