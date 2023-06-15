package com.example.attendance.ExamsModule;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.attendance.R;
import com.example.attendance.Utility.ScanActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Exams_entryActivity extends AppCompatActivity {

    Button button;
    TextView examsTitle, examsTime;
    LinearLayout invigilatorList;
    private ArrayList<String> invList;
    String examsName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_entry);
        examsTitle = findViewById(R.id.exams_name);
        examsTime = findViewById(R.id.exams_time);
        invList = new ArrayList<>();
        invigilatorList = findViewById(R.id.invigilator_list_layout);

        // Retrieve the values from the intent
        Intent intent = getIntent();
        examsName = intent.getStringExtra("departmentName");
        //String invigilator = intent.getStringExtra("invigilator");
        String time = intent.getStringExtra("time");

        // Set the retrieved values to the TextViews
//        examsTitle.setText(examsName);
//        //invigilator.setText(invigilator);
//        examsTime.setText(time);
        loadStaffData();

        button = findViewById(R.id.savebtnExams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Exams_entryActivity.this, ScanActivity.class);
                intent.putExtra("origin","exams");

                startActivity(intent);
            }
        });

    }

    private void addStaffToLayout(String staffName) {
        TextView coursesTextView = new TextView(this);
        coursesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        coursesTextView.setText(staffName);
        coursesTextView.setTextColor(Color.BLACK);
        coursesTextView.setTextSize(20);
        invigilatorList.addView(coursesTextView);
    }

    private void loadStaffData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("exams");
        Query query = examsRef.orderByChild("courseName").equalTo(examsName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // ArrayList<String> invList = new ArrayList<>();
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String examId = examSnapshot.child("examId").getValue(String.class);
                    String courseName = examSnapshot.child("courseName").getValue(String.class);
                    String time = examSnapshot.child("time").getValue(String.class);
                    DataSnapshot invigilatorsSnapshot = examSnapshot.child("invigilators");
                    ArrayList<String> invigilators = new ArrayList<>();
                    for (DataSnapshot invigilatorSnapshot : invigilatorsSnapshot.getChildren()) {
                        String invigilator = invigilatorSnapshot.getValue(String.class);
                        invigilators.add(invigilator);
                        addStaffToLayout(invigilator);
                    }
                    invList.addAll(invigilators);
                    examsTitle.setText(courseName);
                    examsTime.setText(time);
                }
                // Do something with the invList ArrayList
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });
    }

}