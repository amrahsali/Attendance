package com.example.attendance.LoginModule;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.attendance.ExamsModule.ExamsModal;
import com.example.attendance.ExamsModule.ExamsStudentRecordModal;
import com.example.attendance.FacultyModule.CourseModal;
import com.example.attendance.FacultyModule.FacultyModel;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.example.attendance.StudentModule.StudentModal;
import com.example.attendance.Utility.LocalStorageUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Splash_screenActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private DatabaseReference facultyRef;
    private ArrayList<FacultyModel> facultyModelArrayList;

    SharedPreferences sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        facultyRef = FirebaseDatabase.getInstance().getReference("Faculty");
        facultyModelArrayList = new ArrayList<>();

        sharedPreferences = this.getSharedPreferences("com.example.attendance.preferences", Context.MODE_PRIVATE);



        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Show a loading indicator with the message "Updating system from online database"
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating system from online database");
            progressDialog.setCancelable(false); // Prevent users from canceling the progress dialog
            progressDialog.show();

            // Fetch data from Firebase
            checkForUpdates(progressDialog);
        }, 1000);

    }
    @Override
    public void onBackPressed() {
        // Disable the back button functionality in the splash screen activity
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        
    }

    private void fetchDataFromFirebase(ProgressDialog progressDialog) {
        databaseReference.child("Staff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<StaffRVModal> staffList = new ArrayList<>();
                        for (DataSnapshot staffSnapshot : snapshot.getChildren()) {
                            StaffRVModal staff = staffSnapshot.getValue(StaffRVModal.class);
                            staffList.add(staff);
                        }

                        // Save the data to SharedPreferences
                        saveStaffDataToSharedPreferences(staffList);

                        // Dismiss the loading indicator
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch data from Firebase.", Toast.LENGTH_SHORT).show();

                        // Dismiss the loading indicator in case of an error
                        progressDialog.dismiss();
                    }
                });

        databaseReference.child("Student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<StudentModal> studentList = new ArrayList<>();
                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            StudentModal student = studentSnapshot.getValue(StudentModal.class);
                            studentList.add(student);
                        }

                        // Save student data to SharedPreferences
                        saveStudentDataToSharedPreferences(studentList);

                        // Dismiss the loading indicator
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch student data from Firebase.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

//        databaseReference.child("Student")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<StudentModal> studentList = new ArrayList<>();
//                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
//                            StudentModal student = studentSnapshot.getValue(StudentModal.class);
//                            studentList.add(student);
//                        }
//
//                        // Save student data to SharedPreferences
//                        saveStudentDataToSharedPreferences(studentList);
//
//                        // Dismiss the loading indicator
//                        progressDialog.dismiss();
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle the error, e.g., show a toast
//                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch student data from Firebase.", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                });

        databaseReference.child("courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            ArrayList<CourseModal> coursemodalArrayList = new ArrayList<>();
                            for (DataSnapshot coursesSnapshot : snapshot.getChildren()) {
                                CourseModal course = coursesSnapshot.getValue(CourseModal.class);
                                coursemodalArrayList.add(course);
                            }
                            saveCoursesDataToSharedPreferences(coursemodalArrayList);
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(Splash_screenActivity.this, "No courses found in Database", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch courses data from Firebase.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        databaseReference.child("exams")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            ArrayList<ExamsModal> examsModalArrayList = new ArrayList<>();
                            for (DataSnapshot examsSnapshot : snapshot.getChildren()) {
                                ExamsModal exam = examsSnapshot.getValue(ExamsModal.class);
                                examsModalArrayList.add(exam);
                            }
                            saveExamsDataToSharedPreferences(examsModalArrayList);
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(Splash_screenActivity.this, "No courses found in Database", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch courses data from Firebase.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        databaseReference.child("ExamsRecord")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            ArrayList<ExamsStudentRecordModal> examsRecordModalArrayList = new ArrayList<>();
                            for (DataSnapshot examsRecordSnapshot : snapshot.getChildren()) {
                                ExamsStudentRecordModal exam = examsRecordSnapshot.getValue(ExamsStudentRecordModal.class);
                                examsRecordModalArrayList.add(exam);
                            }
                            saveExamsRecordDataToSharedPreferences(examsRecordModalArrayList);
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(Splash_screenActivity.this, "No record found in Database", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch records data from Firebase.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });


        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleDataChange(snapshot);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Splash_screenActivity.this, "Failed to fetch faculty data from Firebase.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


        // Start the new intent after fetching data
        Intent intent = new Intent(Splash_screenActivity.this, Login.class);
        Splash_screenActivity.this.startActivity(intent);
        Splash_screenActivity.this.finish();


    }


    private void handleDataChange(DataSnapshot snapshot) {

        for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
            // Process each faculty and their departments
            processFacultySnapshot(facultySnapshot);
        }
        saveFacultyDataToSharedPreferences(facultyModelArrayList);

    }

    private void processFacultySnapshot(DataSnapshot facultySnapshot) {
        String facultyId = facultySnapshot.getKey();
        String facultyName = facultySnapshot.child("name").getValue(String.class);
        List<String> departmentNames = new ArrayList<>();

        for (DataSnapshot deptSnapshot : facultySnapshot.child("dept").getChildren()) {
            departmentNames.add(deptSnapshot.child("name").getValue(String.class));
        }

        FacultyModel faculty = new FacultyModel(facultyId, facultyName, departmentNames);
        facultyModelArrayList.add(faculty);
    }

    private void saveStaffDataToSharedPreferences(List<StaffRVModal> staffList) {
        String staffDataJson = new Gson().toJson(staffList);
        LocalStorageUtil.saveAndApply(staffDataJson, "staff_data", "system_staff_data", Splash_screenActivity.this );
    }

    private void saveStudentDataToSharedPreferences(List<StudentModal> studentList) {
        String studentDataJson = new Gson().toJson(studentList);
        LocalStorageUtil.saveAndApply(studentDataJson, "student_data", "system_student_data", Splash_screenActivity.this );
    }

    private void saveFacultyDataToSharedPreferences(List<FacultyModel> facultyList) {
        String facultyDataJson = new Gson().toJson(facultyList);
        LocalStorageUtil.saveAndApply(facultyDataJson, "faculty_data", "system_faculty_data", Splash_screenActivity.this );
    }

    private void saveCoursesDataToSharedPreferences(List<CourseModal> courseList) {
        String coursesDataJson = new Gson().toJson(courseList);
        LocalStorageUtil.saveAndApply(coursesDataJson, "course_data", "system_course_data", Splash_screenActivity.this );
    }
    private void saveExamsDataToSharedPreferences(List<ExamsModal> examsList) {
        String examsDataJson = new Gson().toJson(examsList);
        LocalStorageUtil.saveAndApply(examsDataJson, "exams_data", "system_exams_data", Splash_screenActivity.this );
    }
    private void saveExamsRecordDataToSharedPreferences(List<ExamsStudentRecordModal> examsRecordList) {
        String examsRecordDataJson = new Gson().toJson(examsRecordList);
        LocalStorageUtil.saveAndApply(examsRecordDataJson, "exams_record_data", "system_exams_record_data", Splash_screenActivity.this );
    }
    private void checkForUpdates(ProgressDialog progressDialog) {
        DatabaseReference lastUpdatedRef = FirebaseDatabase.getInstance().getReference().child("lastUpdated");
        lastUpdatedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long lastUpdatedFirebaseObj = snapshot.getValue(Long.class);
                    long lastUpdatedFirebase = (lastUpdatedFirebaseObj != null) ? lastUpdatedFirebaseObj : 0;
                    long lastUpdatedLocal = sharedPreferences.getLong("lastUpdated", 0);

                    if (lastUpdatedFirebase > lastUpdatedLocal) {
                        // Firebase data is newer, fetch updates
                        fetchDataFromFirebase(progressDialog);
                    }
                    // No else if needed as if lastUpdatedFirebase is 0, it means the node is newly created or reset
                } else {
                    // 'lastUpdated' node doesn't exist in Firebase, fetch data for the first time
                    fetchDataFromFirebase(progressDialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


}