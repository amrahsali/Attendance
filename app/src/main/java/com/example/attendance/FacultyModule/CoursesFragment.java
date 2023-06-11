package com.example.attendance.FacultyModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.attendance.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;

public class CoursesFragment extends Fragment {

    GridView courseGV;
    ArrayList<Coursemodal> coursemodalArrayList;
    DatabaseReference db;
    CourseAdapter adapter; // Declare adapter as an instance variable



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_courses, container, false);

        courseGV = view.findViewById(R.id.idGVCourses);
        coursemodalArrayList = new ArrayList<>();

        db = FirebaseDatabase.getInstance().getReference();
        loadDataFromFirebase();

        FloatingActionButton addButton = view.findViewById(R.id.courseFABtn);
        addButton.setOnClickListener(v -> showDialogBox());
        adapter = new CourseAdapter(getActivity(), coursemodalArrayList);
        courseGV.setAdapter(adapter);
        return view;
    }
    private void loadDataFromFirebase() {
        db.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Coursemodal course = snapshot.getValue(Coursemodal.class);
                        coursemodalArrayList.add(course);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Course");
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialog_box_course2, null);
        final EditText courseNameEditText = dialogView.findViewById(R.id.edt_name);
        final EditText courseCodeEditText = dialogView.findViewById(R.id.edt_faculty);
        builder.setView(dialogView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseName = courseNameEditText.getText().toString().trim();
                String courseCode = courseCodeEditText.getText().toString().trim();
                collectDataAndDisplay(courseName, courseCode);
            }
        });
        builder.show();
    }
    private void collectDataAndDisplay(String courseName, String courseCode) {
        // Create a new Coursemodal object and set the course name and course code
        Coursemodal course = new Coursemodal();
        course.setCourseName(courseName);
        course.setCodeName(courseCode);

        // Save the course details to Firebase Firestore
        saveCourseToFirebase(courseName, courseCode);

    }
//    private void saveCourseToFirebase(String courseName, String courseCode) {
//        // Create a new document in the "Data" collection with the course details
//        DatabaseReference coursesRef = db.child("courses");
//        String courseId = coursesRef.push().getKey();
//        if (courseId != null) {
//            Coursemodal course = new Coursemodal(courseName, courseCode);
//            coursesRef.child(courseId).setValue(course)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Course saved successfully
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Failed to save course
//                        }
//                    });
//        }
//    }

    private void saveCourseToFirebase(String courseName, String courseCode) {
        DatabaseReference coursesRef = db.child("courses");

        // Convert the course code to lowercase
        String lowercaseCourseCode = courseCode.toLowerCase();

        // Check if the course code already exists (case-insensitive)
        coursesRef.orderByChild("codeName").equalTo(lowercaseCourseCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Course with the given course code already exists
                    Toast.makeText(getActivity(), "Course already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Course does not exist, proceed with saving
                    String courseId = coursesRef.push().getKey();
                    if (courseId != null) {
                        Coursemodal course = new Coursemodal(courseName, courseCode);
                        course.setCodeNameLowerCase(lowercaseCourseCode); // Set the lowercase codeName
                        coursesRef.child(courseId).setValue(course)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Course saved successfully
                                        coursemodalArrayList.add(course);

                                        // Notify the adapter that the data has changed
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "Course saved successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to save course
                                        Toast.makeText(getActivity(), "Failed to save course", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to check course existence", Toast.LENGTH_SHORT).show();
            }
        });
    }




}



