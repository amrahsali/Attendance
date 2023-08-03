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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    String examsName = "";
    String time = "";
    String examsEndTime = "";

    Calendar currentDateTime = Calendar.getInstance();


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
        examsName = intent.getStringExtra("ExamsName");
        //String invigilator = intent.getStringExtra("invigilator");
        time = intent.getStringExtra("time");
        examsEndTime = intent.getStringExtra("examsEndTime");

        Calendar currentDateTime = Calendar.getInstance();

        loadStaffData();
        Date examsDateTime = parseDateTime(time);
        Date examsEndDateTime = parseDateTime(examsEndTime);

        if (examsDateTime.after(currentDateTime.getTime())){
            button.setText("Download records");
        }

        button = findViewById(R.id.savebtnExams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date examsDateTime = parseDateTime(time);

                if (examsDateTime.after(currentDateTime.getTime())) {
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

    // Method to check if the current datetime is 3 hours after the exams datetime
    private boolean isAfterThreeHours(Date examsDateTime) {
        if (examsDateTime == null) {
            return false; // Return false if there's an error parsing the exams datetime
        }

        // Get the current datetime
        Date currentDateTime = Calendar.getInstance().getTime();

        // Calculate the difference in milliseconds between the current datetime and the exams datetime
        long timeDifference = examsDateTime.getTime() - currentDateTime.getTime();

        // Convert the time difference to hours
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifference);

        // Return true if the difference is greater than or equal to 3, false otherwise
        return hoursDifference >= 1;
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
        // You can use examsName and time to create the exams record and make the query.
        // Once you have the list from the query, you can proceed accordingly.
        // You can use Firebase Realtime Database or Firestore to store and query the data.
    }

}