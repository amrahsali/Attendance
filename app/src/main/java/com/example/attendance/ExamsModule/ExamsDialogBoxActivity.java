package com.example.attendance.ExamsModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StudentExamPojo;
import com.example.attendance.StudentModule.StudentModal;

import java.util.ArrayList;
import java.util.List;

public class ExamsDialogBoxActivity extends AppCompatActivity {

    String studentName, matricNo, image, userId;
    boolean courseEligibility;
    TextView StudentNameView, MatricNoView, EligibilityView;
    private final String ELIGIBLE = "ELIGIBLE";
    private final String NOT_ELIGIBLE = "NOT ELIGIBLE";
    ImageView studentProfileImage;
    Button doneButton, nextButton;
    List<StudentExamPojo> eligibleStudentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_dialogbox);
        StudentNameView = findViewById(R.id.exam_std_name);
        MatricNoView = findViewById(R.id.exam_std_matric_no);
        EligibilityView = findViewById(R.id.exams_eligibility);
        studentProfileImage = findViewById(R.id.student_profile_img);
        doneButton = findViewById(R.id.save_exams_to_pdf);
        nextButton = findViewById(R.id.exams_next);

        Intent intent = new Intent();
        studentName = intent.getStringExtra("student_name");
        matricNo = intent.getStringExtra("matricNo");
        courseEligibility = intent.getBooleanExtra("courseEligibility", false);
        image = intent.getStringExtra("img");

        userId = intent.getStringExtra("userId");

        doneButton.setOnClickListener(v -> {

        });
        nextButton.setOnClickListener(v->{

        });
    }

    @Override
    protected void onStart() {
        if (!studentName.isEmpty()){
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
        super.onStart();
    }


    private void addToEligibleStudentsList() {
        Intent intent = getIntent();
        studentName = intent.getStringExtra("student_name");
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
}