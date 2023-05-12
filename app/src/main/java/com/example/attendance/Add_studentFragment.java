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

   private FirebaseAuth mAuth;
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
    TextView name,email;
    private static final int ADD_COURSE_REQUEST = 1;
    private RecyclerView studentRV;
    private StudentModal studentRVModal;
    private StudentAdapter studentAdapter;
    private ArrayList<StudentModal> studentRVModalArrayList;



 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container,
                          Bundle savedInstanceState) {

  mAuth = FirebaseAuth.getInstance();
  View view =  inflater.inflate(R.layout.fragment_add_student, container, false);

  firebaseDatabase = FirebaseDatabase.getInstance();
  databaseReference = firebaseDatabase.getReference("Student");
  FloatingActionButton fab = view.findViewById(R.id.stdFABtn);
  studentRVModalArrayList = new ArrayList<>();
  mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
  studentAdapter = new StudentAdapter(studentRVModalArrayList, getContext());
  studentRV = view.findViewById(R.id.idRVStudent);
  studentRV.setLayoutManager(new LinearLayoutManager(getContext()));
  studentRV.setHasFixedSize(true);
  studentRV.setAdapter(studentAdapter);
  getStudent();


  // adding on click listener for floating action button.
  fab.setOnClickListener((View v) -> {
   // starting a new activity for adding a new course
   // and passing a constant value in it.
   Intent intent1 = new Intent(getActivity(), StudentAddition.class);
   startActivity(intent1);



  });

  return view;
 }

 private void getStudent() {
  //on below line clearing our list.
  studentRVModalArrayList.clear();
  Query query = databaseReference.orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
  //on below line we are calling add child event listener method to read the data.
  query.addChildEventListener(new ChildEventListener() {
   @Override
   public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    //on below line we are hiding our progress bar.
    //loadingPB.setVisibility(View.GONE);
    //adding snapshot to our array list on below line.
    studentRVModalArrayList.add(snapshot.getValue(StudentModal.class));
    //notifying our adapter that data has changed.
    studentAdapter.notifyDataSetChanged();
   }

   @Override
   public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    studentAdapter.notifyDataSetChanged();

   }

   @Override
   public void onChildRemoved(@NonNull DataSnapshot snapshot) {
    studentAdapter.notifyDataSetChanged();

   }

   @Override
   public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    studentAdapter.notifyDataSetChanged();

   }

   @Override
   public void onCancelled(@NonNull DatabaseError error) {

   }
  });
 }

  }


