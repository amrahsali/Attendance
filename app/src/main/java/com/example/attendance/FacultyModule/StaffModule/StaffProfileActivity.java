package com.example.attendance.FacultyModule.StaffModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.attendance.R;
import com.example.attendance.ScanActivity;

public class StaffProfileActivity extends AppCompatActivity {

    TextView username, depaprtment;
    ImageView takeAttendance, addStudent, viewRecords, examination, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);
        username = findViewById(R.id.username);
        depaprtment = findViewById(R.id.department);
        profile = findViewById(R.id.staff_profile);
        takeAttendance = findViewById(R.id.take_attendance_btn);
        addStudent = findViewById(R.id.add_student_btn);
        viewRecords = findViewById(R.id.viewrecords_btn);
        examination = findViewById(R.id.examination_btn);

        takeAttendance.setOnClickListener(v -> {
            Intent i = new Intent(StaffProfileActivity.this, ScanActivity.class);
            i.putExtra("origin","staffProfile");
            startActivity(i);
        });


    }
}