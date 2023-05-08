package com.example.attendance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    FloatingActionButton addStudentFAB;

    private FirebaseAuth mAuth;
    Button create_staff;
    EditText username, phoneNumber, emailad, department1, faculty1;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;
    private static final int ADD_COURSE_REQUEST = 1;
    private RecyclerView coursesRV;
    private StudentModal staffRVModal;
    private StudentAdapter studentAdapter;
    private ArrayList<StudentModal> studentModalArrayList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_student, container, false);

        addStudentFAB = view.findViewById(R.id.stdFABtn);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("student");
        FloatingActionButton fab = view.findViewById(R.id.stdFABtn);
        studentModalArrayList = new ArrayList<>();
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        studentAdapter = new StudentAdapter(studentModalArrayList, getContext());
        coursesRV = view.findViewById(R.id.idRVStudent);
        coursesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRV.setHasFixedSize(true);
        coursesRV.setAdapter(studentAdapter);
        getStudent();



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a new activity for adding a course.
                Intent i = new Intent(getContext(), Student_profileActivity.class);
                startActivity(i);
            }
        });


        return view;

    }

    private void getStudent() {
        //on below line clearing our list.
        studentModalArrayList.clear();
        Query query = databaseReference.orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        //on below line we are calling add child event listener method to read the data.
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //on below line we are hiding our progress bar.
                //loadingPB.setVisibility(View.GONE);
                //adding snapshot to our array list on below line.
                studentModalArrayList.add(snapshot.getValue(StudentModal.class));
                //notifying our adapter that data has changed.
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.
                //loadingPB.setVisibility(View.GONE);
                studentAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying our adapter when child is removed.
                studentAdapter.notifyDataSetChanged();
                //loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //notifying our adapter when child is moved.
                studentAdapter.notifyDataSetChanged();
                //loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}


