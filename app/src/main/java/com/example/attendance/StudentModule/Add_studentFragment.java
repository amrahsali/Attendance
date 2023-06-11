package com.example.attendance.StudentModule;


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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Add_studentFragment extends Fragment {

    private FirebaseAuth mAuth;
    private static String TAG;
    private boolean hasData = false;
    Button create_student;
    EditText username, phoneNumber, emailad, department1, faculty1;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name, email;
    private static final int ADD_COURSE_REQUEST = 1;
    private RecyclerView studentRV;
    private StudentModal studentRVModal;
    private TextView emptyTextView;
    private StudentAdapter studentAdapter;
    private Handler handler;
    private Runnable timeoutRunnable;

    // Declare a member variable for the ChildEventListener
    private ChildEventListener childEventListener;
    private ArrayList<StudentModal> studentRVModalArrayList;
    private ArrayList<StudentModal> filteredList = new ArrayList<>();
    Spinner levelSpinner, falcSpinner, deptSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        levelSpinner = view.findViewById(R.id.level_spinner);
        falcSpinner = view.findViewById(R.id.faculty_spinner);
        deptSpinner = view.findViewById(R.id.dept_spinner);
        loadingPB = view.findViewById(R.id.loading_pb);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        studentRVModalArrayList = new ArrayList<>();
        FloatingActionButton fab = view.findViewById(R.id.stdFABtn);
        studentAdapter = new StudentAdapter(studentRVModalArrayList, getContext());
        studentRV = view.findViewById(R.id.idRVStudent);
        studentRV.setLayoutManager(new LinearLayoutManager(getContext()));
        studentRV.setHasFixedSize(true);
        studentRV.setAdapter(studentAdapter);
        getStudent();
        initspinners();


        if (isUserLoggedIn()){
            fab.setOnClickListener((View v) -> {
                Intent intent1 = new Intent(getActivity(), StudentAddition.class);
                startActivity(intent1);
            });
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }

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
        Query query = databaseReference.orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        //on below line we are calling add child event listener method to read the data.
        startTimeoutRunnable(query);

        query.addChildEventListener(childEventListener = new ChildEventListener() {
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

    public void filterData(FilterModel filterModel) {
        filteredList.clear();

        for (StudentModal student : studentRVModalArrayList) {
            boolean isMatched = filterModel.getLevel().equalsIgnoreCase("Select level") || filterModel.getLevel().equalsIgnoreCase(student.getStudentLevel());

            if (!filterModel.getDepartment().equals("Select dept") && !filterModel.getDepartment().equalsIgnoreCase(student.getStudentDepartment())) {
                isMatched = false;
            }

            if (!filterModel.getFaculty().equals("Select fal") && !filterModel.getFaculty().equalsIgnoreCase(student.getStudentFaculty())) {
                isMatched = false;
            }

            if (isMatched) {
                filteredList.add(student);
            }
        }

        // Check if no dropdown is selected, add all students
        if (filterModel.getLevel().equals("Select level") && filterModel.getDepartment().equals("Select dept") && filterModel.getFaculty().equals("Select fal")) {
            filteredList.addAll(studentRVModalArrayList);
        }

        studentAdapter.setStudentList(filteredList);
        studentAdapter.notifyDataSetChanged();
    }


    private void initspinners() {
        String[] levels = {"Select level", "100", "200", "300", "400", "500"};
        String[] fal = {"Select fal", "Computing", "Engineering", "Enviromental", "FAMSS"};
        String[] dept = {"Select dept", "Software engineering", "Cyber security", "Info tech", "Info System"};

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, levels);
        ArrayAdapter<String> falcAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fal);
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dept);

        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        falcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        levelSpinner.setAdapter(levelAdapter);
        falcSpinner.setAdapter(falcAdapter);
        deptSpinner.setAdapter(deptAdapter);

        levelSpinner.setSelection(0); // Set the default selection to the first item
        falcSpinner.setSelection(0);
        deptSpinner.setSelection(0);

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLevel = (String) parent.getItemAtPosition(position);
                String selectedFaculty = (String) falcSpinner.getSelectedItem();
                String selectedDepartment = (String) deptSpinner.getSelectedItem();
                if (!selectedLevel.equalsIgnoreCase("Select level")) {
                    FilterModel filterModel = new FilterModel(selectedLevel, selectedFaculty, selectedDepartment);
                    Log.i(TAG, "onItemSelected: " + filterModel);
                    Toast.makeText(getContext(), filterModel.getLevel(), Toast.LENGTH_SHORT).show();
                    filterData(filterModel);
                } else {
                    // Reset the filter when "Select level" is selected
                    FilterModel filterModel = new FilterModel("Select level", selectedFaculty, selectedDepartment);
                    filterData(filterModel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

// Similar implementation for falcSpinner and deptSpinner


        // Rest of the code...
    }

    // Method to start the timeout runnable
    private void startTimeoutRunnable(Query query) {
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
                query.removeEventListener(childEventListener);
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


}


