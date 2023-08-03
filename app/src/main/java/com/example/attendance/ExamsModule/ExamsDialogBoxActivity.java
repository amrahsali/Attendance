package com.example.attendance.ExamsModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StudentExamPojo;
import com.example.attendance.StudentModule.StudentModal;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExamsDialogBoxActivity extends AppCompatActivity {

    String studentName, matricNo, image, userId, examsName, examsTime;
    boolean courseEligibility;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView StudentNameView, MatricNoView, EligibilityView;
    private final String ELIGIBLE = "ELIGIBLE";
    private final String NOT_ELIGIBLE = "NOT ELIGIBLE";
    ImageView studentProfileImage;
    Button doneButton, nextButton;
    List<StudentExamPojo> eligibleStudentsList = new ArrayList<>();
    Intent intent ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        setContentView(R.layout.activity_exams_dialogbox);
        StudentNameView = findViewById(R.id.exam_std_name);
        MatricNoView = findViewById(R.id.exam_std_matric_no);
        EligibilityView = findViewById(R.id.exams_eligibility);
        studentProfileImage = findViewById(R.id.student_profile_img);
        doneButton = findViewById(R.id.save_exams_to_pdf);
        nextButton = findViewById(R.id.exams_next);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Staff");


//        Toast.makeText(ExamsDialogBoxActivity.this, "got here " + intent.getStringExtra("studentName"), Toast.LENGTH_SHORT).show();
        if (intent.hasExtra("studentName")){

            studentName = intent.getStringExtra("studentName");
            matricNo = intent.getStringExtra("matricNo");
            courseEligibility = intent.getBooleanExtra("courseEligibility", false);
            image = intent.getStringExtra("img");
            userId = intent.getStringExtra("userId");
            examsName = intent.getStringExtra("examsName");
            examsTime = intent.getStringExtra("examsTime");

            StudentNameView.setText(studentName);
            MatricNoView.setText(matricNo);
            if (courseEligibility){
                EligibilityView.setText(ELIGIBLE);
                EligibilityView.setTextColor(Color.GREEN);
            }else {
                EligibilityView.setText(NOT_ELIGIBLE);
                EligibilityView.setTextColor(Color.RED);
            }
            Glide.with(ExamsDialogBoxActivity.this).load(image).into(studentProfileImage);
        }

        doneButton.setOnClickListener(v -> {
            saveEligibleStudentsToFirebase();
        });
        nextButton.setOnClickListener(v->{
            addToEligibleStudentsList();
            Intent gotoScan = new Intent(ExamsDialogBoxActivity.this, ExamsScanActivity.class);
            startActivity(gotoScan);
            nextButton.setEnabled(false);
        });
    }

    @Override
    protected void onStop() {
        //eligibleStudentsList.clear();
        super.onStop();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }


    private void addToEligibleStudentsList() {
        Intent intent = getIntent();
        studentName = intent.getStringExtra("studentName");
        matricNo = intent.getStringExtra("matricNo");
        courseEligibility = intent.getBooleanExtra("courseEligibility", false);
        image = intent.getStringExtra("img");
        userId = intent.getStringExtra("userId");

        if (studentName != null && !studentName.isEmpty()) {

            StudentExamPojo student = new StudentExamPojo();
            student.setStudentName(studentName);
            student.setUserId(userId);
            student.setMatNumber(matricNo);
            // Add the student to the eligibleStudentsList
            eligibleStudentsList.add(student);
        }
    }

    private void saveEligibleStudentsToFirebase() {
        // Check if the eligibleStudentsList is not empty
        if (!eligibleStudentsList.isEmpty()) {
            DatabaseReference examsRecordRef = firebaseDatabase.getReference("ExamsRecord");
            DatabaseReference examRef = examsRecordRef.child(examsName + "-" + examsTime);

            for (StudentExamPojo student : eligibleStudentsList) {
                // Generate a unique key for each student in the EligibleStudents node
                String key = examRef.push().getKey();
                if (key != null) {
                    // Save the student object to Firebase with the generated key
                    examRef.child(key).setValue(student);
                }
            }
            // Show a success message
            Toast.makeText(ExamsDialogBoxActivity.this, "Exams Record Saved.", Toast.LENGTH_SHORT).show();
            // Clear the eligibleStudentsList after saving to Firebase
            eligibleStudentsList.clear();
        } else {
            // Show a message if there are no eligible students to save
            Toast.makeText(ExamsDialogBoxActivity.this, "No student to save.", Toast.LENGTH_SHORT).show();
        }
    }





}