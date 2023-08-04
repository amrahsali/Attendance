package com.example.attendance.ExamsModule;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.Utility.ScanActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Exams_entryActivity extends AppCompatActivity {

    Button button;
    TextView examsTitle, examsTime;
    LinearLayout invigilatorList;
    private ArrayList<String> invList;

    ArrayList<ExamsStudentRecordModal> examsModalsArrayList;
    String examsName = "";
    String time = "";
    String examsEndTime = "";

    RecyclerView examsRV;

    DatabaseReference examsRecordRef;

    ExamsStudentRecordAdapter examsRecordAdapter;

    Calendar currentDateTime = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_entry);
        examsTitle = findViewById(R.id.exams_name);
        examsTime = findViewById(R.id.exams_time);
        invList = new ArrayList<>();
        invigilatorList = findViewById(R.id.invigilator_list_layout);
        examsModalsArrayList = new ArrayList<>();
        examsRV = findViewById(R.id.examsRecordlist);
        examsRecordAdapter = new ExamsStudentRecordAdapter(examsModalsArrayList, Exams_entryActivity.this);

        examsRV.setLayoutManager(new LinearLayoutManager(this));
        examsRV.setAdapter(examsRecordAdapter);

        // Retrieve the values from the intent
        Intent intent = getIntent();
        examsName = intent.getStringExtra("ExamsName");
        //String invigilator = intent.getStringExtra("invigilator");
        time = intent.getStringExtra("time");
        examsEndTime = intent.getStringExtra("examsEndTime");

        Calendar currentDateTime = Calendar.getInstance();

        loadStaffData();

        Date examsEndDateTime = parseDateTime(examsEndTime);

        if (examsEndDateTime != null){
            if (examsEndDateTime.after(currentDateTime.getTime())){
                button.setText(R.string.download_records);
            }
        }

        Log.i(TAG, "onCreate: exams time is: " + examsEndDateTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "onCreate: current time is: " + currentDateTime.getTime());
        }


        button = findViewById(R.id.savebtnExams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (examsEndDateTime.after(currentDateTime.getTime())) {
                    // Generate exams record and make a query to Firebase
                    generateExamsRecordAndQueryFirebase(examsName, time);
                } else {
                    // If it's not yet 3 hours after the exams datetime, simply start the ExamsScanActivity
                    Intent intent = new Intent(Exams_entryActivity.this, ExamsScanActivity.class);
                    intent.putExtra("ExamsName", examsName);
                    intent.putExtra("ExamsTime", time);
                    startActivity(intent);
                }
            }
        });

        loadExamsRecordData();

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

    // Method to parse the datetime string into a Date object
    private Date parseDateTime(String dateTimeString) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        try {
            return format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void generateExamsRecordAndQueryFirebase(String examsName, String time) {
        // Code to generate the exams record and make a query to Firebase here.

    }

    private void loadExamsRecordData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("ExamsRecord");

        Query query = examsRef.orderByKey(); // Assuming you want to order by the keys (coursetitle+date)

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseKey = courseSnapshot.getKey();
                    if (courseKey.startsWith(examsName + "-" + null)) {
                        for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            String studentName = studentSnapshot.child("student-name").getValue(String.class);
                            String studentMatNum = studentSnapshot.child("matNumber").getValue(String.class);

                            examsModalsArrayList.add(new ExamsStudentRecordModal(studentName, studentMatNum));
                            examsModalsArrayList.add(new ExamsStudentRecordModal("amrah sali", "studentMatNum"));

                        }
                    }
                }



//                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
//                    String xcourseKey = courseSnapshot.getKey();
//                    Toast.makeText(Exams_entryActivity.this, "exams is: " + courseSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Exams_entryActivity.this, "exams is: " + examsName + "-" + null, Toast.LENGTH_SHORT).show();
//
//                    if (xcourseKey.startsWith(examsName + "-" + null)) {
//                        for (DataSnapshot studentRecordSnapshot : dataSnapshot.getChildren()) {
//                            String courseKey = courseSnapshot.getKey();
//                            if (courseKey.startsWith(desiredCourseName)) {
//                                for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
//                                    String studentId = studentSnapshot.getKey();
//                                    String studentName = studentSnapshot.child("student-name").getValue(String.class);
//
//                                    studentList.add(new Student(studentId, studentName));
//                                }
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });

    }

}