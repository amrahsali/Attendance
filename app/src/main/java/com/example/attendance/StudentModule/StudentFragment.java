package com.example.attendance.StudentModule;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendance.FacultyModule.FacultyModel;
import com.example.attendance.LoginModule.Splash_screenActivity;
import com.example.attendance.R;
import com.example.attendance.Utility.LocalStorageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentFragment extends Fragment {

    private FirebaseAuth mAuth;
    private boolean hasData = false;
    private ProgressBar loadingPB;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private RecyclerView studentRV;
    private TextView emptyTextView;
    private StudentAdapter studentAdapter;
    private Handler handler;
    private Runnable timeoutRunnable;

    // Declare a member variable for the ChildEventListener
    private ChildEventListener childEventListener;
    private ArrayList<StudentModal> studentRVModalArrayList;
    private ArrayList<StudentModal> filteredList = new ArrayList<>();
    Spinner levelSpinner, facultySpinner, departmentSpinner;
    private Map<String, List<String>> facultyDepartmentsMap;
    private ArrayAdapter<String> departmentAdapter, levelAdapter, facultyAdapter;

    private String selectedLevel = "All";
    private String selectedFaculty = "All";
    private String selectedDepartment = "All";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        FloatingActionButton fab = view.findViewById(R.id.stdFABtn);
        onInit(view);

        if (isUserLoggedIn()){
            fab.setOnClickListener((View v) -> {
                Intent intent1 = new Intent(getActivity(), StudentAddition.class);
                startActivity(intent1);
            });
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }
        loadFacultyData();

        loadStudentsFromLocalStorage();
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
         new Handler().postDelayed(() -> {
          refreshData();
          swipeRefreshLayout.setRefreshing(false);
         }, 100); // 10 seconds delay

        });
        return view;
    }

    private void getStudent() {
        Log.d("Student Fragment", "loading values from firebase");
        handler = new Handler();
        loadingPB.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        studentRVModalArrayList.clear();
        startTimeoutRunnable();

        databaseReference.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                studentRVModalArrayList.add(snapshot.getValue(StudentModal.class));
                //notifying our adapter that data has changed.
                studentAdapter.notifyDataSetChanged();
                hasData = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopTimeoutRunnable();
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Error: " + error.getMessage());
            }
        });
    }

    private void startTimeoutRunnable() {
        // Initialize the timeout runnable
        timeoutRunnable = () -> {
            // Hide the loading progress bar
            loadingPB.setVisibility(View.GONE);

            // Show the error message
            if (studentRVModalArrayList.isEmpty()){
                emptyTextView.setVisibility(View.VISIBLE);
//                emptyTextView.setText("No Student Found");
            }


            // Remove the child event listener
            databaseReference.removeEventListener(childEventListener);
        };

        // Schedule the runnable after 10 seconds
        handler.postDelayed(timeoutRunnable, 10000);
    }
    // Method to stop the timeout runnable
    private void stopTimeoutRunnable() {
        // Remove the timeout runnable callbacks
        handler.removeCallbacks(timeoutRunnable);
    }

    private void refreshData() {
        // Clear the existing data
        studentRVModalArrayList.clear();
        studentAdapter.notifyDataSetChanged();

        // Call the method to fetch the updated data
        fetchAndUpdateData();
    }

    private void fetchAndUpdateData() {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Student");
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<StudentModal> studentList = new ArrayList<>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    StudentModal student = studentSnapshot.getValue(StudentModal.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                updateLocalStorage(studentList);
                loadStudentsFromLocalStorage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch student data: " + databaseError.getMessage());
            }
        });
    }

    private void updateLocalStorage(List<StudentModal> studentList) {
        String studentDataJson = new Gson().toJson(studentList);
        LocalStorageUtil.saveAndApply(studentDataJson, "student_data", "system_student_data", getActivity() );
    }

    private boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void onInit(View view){
        loadingPB = view.findViewById(R.id.loading_pb);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        studentRVModalArrayList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentRVModalArrayList, getContext());
        facultyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        departmentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        levelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentRV = view.findViewById(R.id.idRVStudent);
        studentRV.setLayoutManager(new LinearLayoutManager(getContext()));
        studentRV.setHasFixedSize(true);
        studentRV.setAdapter(studentAdapter);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
    }

    private void loadFacultyData() {
        List<FacultyModel> facultyList = LocalStorageUtil.retrieveFacultyDataFromLocalStorage(getActivity());
        facultyDepartmentsMap = new HashMap<>();
        for (FacultyModel faculty : facultyList) {
            String facultyName = faculty.getName();
            List<String> departmentNames = faculty.getDept(); // Assuming FacultyModel has a method getDept()

            if (facultyName != null && departmentNames != null && !departmentNames.isEmpty()) {
                facultyDepartmentsMap.put(facultyName, departmentNames);
            }
        }

        facultyAdapter.clear();
        facultyAdapter.addAll(facultyDepartmentsMap.keySet());
        facultyAdapter.notifyDataSetChanged();
    }

    private void loadStudentsFromLocalStorage() {
        List<StudentModal> students = LocalStorageUtil.retrieveStudentDataFromLocalStorage(getContext());

        if (students != null && !students.isEmpty()) {
            studentRVModalArrayList.clear();
            studentRVModalArrayList.addAll(students);
            Log.d("Student Fragment", "loaded values from localStorage: " + students);
            studentAdapter.notifyDataSetChanged();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No Student Found");
        }
    }



}


