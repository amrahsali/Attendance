package com.example.attendance.StudentModule;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendance.FilterModel;
import com.example.attendance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Add_studentFragment extends Fragment {

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
    private ArrayAdapter<String> facultyAdapter;
    private ArrayAdapter<String> departmentAdapter;
    private ArrayAdapter<String> levelAdapter;

    private String selectedLevel = "All";
    private String selectedFaculty = "All";
    private String selectedDepartment = "All";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        FloatingActionButton fab = view.findViewById(R.id.stdFABtn);
        onInit(view);
        getStudent();
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
        loadLevelData();
        spinnerListeners();
//        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedFaculty = (String) parent.getItemAtPosition(position);
//                updateDepartmentDropdown(selectedFaculty);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });


        // Find the SwipeRefreshLayout in the layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Set the listener for the swipe-to-refresh action
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call the method to refresh the data
             // Stop the refreshing animation after 10 seconds
             new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
               // Stop the refreshing animation
               refreshData();
               swipeRefreshLayout.setRefreshing(false);
                  // Check if there is data available
                  if (hasData) {
                      emptyTextView.setVisibility(View.GONE);
                  } else {
                      emptyTextView.setVisibility(View.VISIBLE);
                      emptyTextView.setText("No Student Found");
                  }
              }
             }, 100); // 10 seconds delay

            }
        });
        return view;
    }

    private void getStudent() {
        handler = new Handler();
        //on below line clearing our list.
        loadingPB.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        studentRVModalArrayList.clear();
        //Query query = databaseReference.orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        //on below line we are calling add child event listener method to read the data.
        startTimeoutRunnable();

        databaseReference.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //on below line we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                //adding snapshot to our array list on below line.
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

//    private void getStudent() {
//        // Clear the existing data
//        studentRVModalArrayList.clear();
//
//        // Add a listener to fetch the student data
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Iterate over the student data
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    StudentModal student = dataSnapshot.getValue(StudentModal.class);
//
//                    // Apply filters
//                    if (selectedLevel.equals("All") || student.getStudentLevel().equals(selectedLevel)) {
//                        if (selectedFaculty.equals("All") || student.getStudentFaculty().equals(selectedFaculty)) {
//                            if (selectedDepartment.equals("All") || student.getStudentDepartment().equals(selectedDepartment)) {
//                                studentRVModalArrayList.add(student);
//                            }
//                        }
//                    }
//                }
//
//                // Notify the adapter that the data has changed
//                studentAdapter.notifyDataSetChanged();
//
//                // Check if there is data available
//                if (studentRVModalArrayList.isEmpty()) {
//                    emptyTextView.setVisibility(View.VISIBLE);
//                    emptyTextView.setText("No Student Found");
//                } else {
//                    emptyTextView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Failed to fetch student data: " + error.getMessage());
//            }
//        });
//    }

    // Method to start the timeout runnable
    private void startTimeoutRunnable() {
        // Initialize the timeout runnable
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Hide the loading progress bar
                loadingPB.setVisibility(View.GONE);

                // Show the error message
                if (studentRVModalArrayList.isEmpty()){
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No Student Found");
                }


                // Remove the child event listener
                databaseReference.removeEventListener(childEventListener);
            }
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
        getStudent();
    }

    private boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void onInit(View view){
        levelSpinner = view.findViewById(R.id.level_spinner);
        facultySpinner = view.findViewById(R.id.faculty_spinner);
        departmentSpinner = view.findViewById(R.id.dept_spinner);
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
        facultySpinner.setAdapter(facultyAdapter);
        departmentSpinner.setAdapter(departmentAdapter);
        levelSpinner.setAdapter(levelAdapter);
        studentRV = view.findViewById(R.id.idRVStudent);
        studentRV.setLayoutManager(new LinearLayoutManager(getContext()));
        studentRV.setHasFixedSize(true);
        studentRV.setAdapter(studentAdapter);
    }

    private void loadFacultyData() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("Faculty");
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                facultyDepartmentsMap = new HashMap<>(); // Initialize the map
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                    String facultyName = facultySnapshot.child("name").getValue(String.class);
                    List<String> departmentNames = new ArrayList<>();
                    for (DataSnapshot deptSnapshot : facultySnapshot.child("dept").getChildren()) {
                        String departmentName = deptSnapshot.child("name").getValue(String.class);
                        if (departmentName != null) {
                            departmentNames.add(departmentName);
                        }
                    }
                    if (facultyName != null && !departmentNames.isEmpty()) {
                        facultyDepartmentsMap.put(facultyName, departmentNames);
                    }
                }
                facultyAdapter.addAll(facultyDepartmentsMap.keySet());
                facultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load faculty data: " + databaseError.getMessage());
            }
        });
    }

    private void loadLevelData(){
        List<String> levels = new ArrayList<>(Arrays.
                asList("100", "200", "300", "400", "500"));
        levelAdapter.addAll(levels);
        levelAdapter.notifyDataSetChanged();
    }

    private void updateDepartmentDropdown(String faculty) {
        departmentAdapter.clear();

        if (!faculty.equals("Select Faculty")) {
            List<String> departmentNames = facultyDepartmentsMap.get(faculty);
            if (departmentNames != null) {
                departmentAdapter.addAll(departmentNames);
            }
        }

        departmentAdapter.notifyDataSetChanged();
    }

    private void spinnerListeners(){
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLevel = (String) parent.getItemAtPosition(position);
                getStudent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFaculty = (String) parent.getItemAtPosition(position);
                updateDepartmentDropdown(selectedFaculty);
                getStudent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = (String) parent.getItemAtPosition(position);
                getStudent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }


}


