package com.example.attendance.ExamsModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendance.ExamsModule.ExamsAdapter;

//import com.example.attendance.FacultyModule.CourseAdapter;
import com.example.attendance.FacultyModule.CourseAdapter;
import com.example.attendance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExaminationFragment extends Fragment {

    RecyclerView examsRV;
    ArrayList<ExamsModal> examsModalsArrayList;
    ExamsAdapter examsAdapter;
    DatabaseReference examsRef;

    FirebaseAuth mAuth;


    public static ExaminationFragment newInstance() {
        return new ExaminationFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_examination, container, false);

        examsRV = view.findViewById(R.id.idRVExams);
        mAuth = FirebaseAuth.getInstance();
        examsModalsArrayList = new ArrayList<>();
        examsAdapter = new ExamsAdapter(examsModalsArrayList, getContext());

        examsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        examsRV.setAdapter(examsAdapter);

        examsRef = FirebaseDatabase.getInstance().getReference().child("exams");

        loadExamsData();

        if (isUserLoggedIn()){
            view.findViewById(R.id.examsFABtn).setOnClickListener(v -> showDialogBox());
            view.findViewById(R.id.examsFABtn).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.examsFABtn).setVisibility(View.GONE);
        }

        return view;
    }
    private void loadExamsData() {
        examsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                ExamsModal exam = dataSnapshot.getValue(ExamsModal.class);
                examsModalsArrayList.add(exam);
                examsAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Other overridden methods...

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load exams data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Exam");
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialogbox_examsadd, null);
        final EditText examNameEditText = dialogView.findViewById(R.id.edt_nameExam);
        builder.setView(dialogView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String examName = examNameEditText.getText().toString().trim();
                saveExamToFirebase(examName);
            }
        });
        builder.show();
    }

    private void saveExamToFirebase(String departmentName) {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference().child("exams");

        String examId = examsRef.push().getKey();
        if (examId != null) {
            ExamsModal exam = new ExamsModal(examId, departmentName);
            examsRef.child(departmentName).setValue(exam).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Exam added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to add exam", Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }



}