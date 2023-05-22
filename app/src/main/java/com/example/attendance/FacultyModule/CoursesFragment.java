package com.example.attendance.FacultyModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.attendance.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    FirebaseFirestore db;
    CourseAdapter adapter; // Declare adapter as an instance variable

    private void collectDataAndDisplay(String courseName, String courseCode) {
        // Create a new Coursemodal object and set the course name and course code
        Coursemodal course = new Coursemodal();
        course.setCourseName(courseName);
        course.setCodeName(courseCode);

        coursemodalArrayList.add(course);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

        // Save the course details to Firebase Firestore
        saveCourseToFirestore(courseName, courseCode);

    }
    private void saveCourseToFirestore(String courseName, String courseCode) {
        // Create a new document in the "Data" collection with the course details
        db.collection("Data")
                .add(new Coursemodal(courseName, courseCode))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Course saved successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save course
                    }
                });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_courses, container, false);

        courseGV = view.findViewById(R.id.idGVCourses);
        coursemodalArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // here we are calling a method
        // to load data in our list view.
        loadDatainGridView();

        FloatingActionButton addButton = view.findViewById(R.id.courseFABtn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });
        adapter = new CourseAdapter(getActivity(), coursemodalArrayList);
        courseGV.setAdapter(adapter);


        return view;

    }
    private void loadDatainGridView(){

        db.collection("Data").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding our
                            // progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                // after getting this list we are passing
                                // that list to our object class.
                                Coursemodal dataModal = d.toObject(Coursemodal.class);

                                // after getting data from Firebase
                                // we are storing that data in our array list
                                coursemodalArrayList.add(dataModal);
                            }
                            // after that we are passing our array list to our adapter class.
                            CourseAdapter adapter = new CourseAdapter(getActivity(),  coursemodalArrayList);

                            // after passing this array list
                            // to our adapter class we are setting
                            // our adapter to our list view.
                            courseGV.setAdapter(adapter);
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity(),  "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity(),  "Fail to load data..", Toast.LENGTH_SHORT).show();
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
}



