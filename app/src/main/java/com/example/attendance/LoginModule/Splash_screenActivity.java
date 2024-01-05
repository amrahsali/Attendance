package com.example.attendance.LoginModule;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.example.attendance.StudentModule.StudentModal;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
            fetchDataFromFirebase(progressDialog);
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
                        saveDataToSharedPreferences(staffList);

                        // Dismiss the loading indicator
                        progressDialog.dismiss();

                        // Start the new intent after fetching data
                        Intent intent = new Intent(Splash_screenActivity.this, Login.class);
                        Splash_screenActivity.this.startActivity(intent);
                        Splash_screenActivity.this.finish();
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

                        // Start the new intent after fetching data
                        Intent intent = new Intent(Splash_screenActivity.this, Login.class);
                        Splash_screenActivity.this.startActivity(intent);
                        Splash_screenActivity.this.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error, e.g., show a toast
                        Toast.makeText(Splash_screenActivity.this, "Failed to fetch student data from Firebase.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void saveDataToSharedPreferences(List<StaffRVModal> staffList) {
        // Convert the list to JSON using Gson
        String staffDataJson = new Gson().toJson(staffList);

        // Save the JSON string to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("system_staff_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("staff_data", staffDataJson);
        editor.apply();
    }

    private void saveStudentDataToSharedPreferences(List<StudentModal> studentList) {

        String studentDataJson = new Gson().toJson(studentList);

        SharedPreferences sharedPreferences = getSharedPreferences("system_student_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("student_data", studentDataJson);
        editor.apply();
    }


}