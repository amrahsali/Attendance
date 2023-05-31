package com.example.attendance.ExamsModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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


    public static ExaminationFragment newInstance() {
        return new ExaminationFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_examination, container, false);

        examsRV = view.findViewById(R.id.idRVExams);
        examsModalsArrayList = new ArrayList<>();
        examsAdapter = new ExamsAdapter(examsModalsArrayList);

        examsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        examsRV.setAdapter(examsAdapter);

        examsRef = FirebaseDatabase.getInstance().getReference().child("exams");

        loadExamsData();

        view.findViewById(R.id.examsFABtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });

        return view;
    }
    private void loadExamsData() {
        examsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                ExamsModal exam = dataSnapshot.getValue(ExamsModal.class);
                examsModalsArrayList.add(exam);
                examsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle updated exam data if needed
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removed exam data if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Handle moved exam data if needed
            }

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

    private void saveExamToFirebase(String examName) {
        String examId = examsRef.push().getKey();
        if (examId != null) {
            ExamsModal exam = new ExamsModal(examId, examName);
            examsRef.child(examId).setValue(exam);
        }
    }



}