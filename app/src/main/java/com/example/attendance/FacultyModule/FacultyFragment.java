package com.example.attendance.FacultyModule;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.attendance.R;

public class FacultyFragment extends Fragment {

    Dialog dialog;

    RecyclerView recyclerView;
    FacultyAdapter adapter;
    ArrayList<FacultyModel> facultyList;

    EditText editText;



    public FacultyFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        dialog = new Dialog(getContext());
        facultyList = new ArrayList<>();

        adapter = new FacultyAdapter(getContext(), facultyList);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faculty, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.idRVFacultyt);
        Context context = requireContext();

        FacultyAdapter adapter = new FacultyAdapter(context, facultyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(adapter);


        adapter = new FacultyAdapter(facultyList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.facultyFABtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacultyDialog();

            }


        });



    return view;
}

    private void showFacultyDialog() {
        dialog.setContentView(R.layout.activity_faculty_dialogbox);
        dialog.create();
        Button saveButton = dialog.findViewById(R.id.add_faculty_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText facultyNameEditText = dialog.findViewById(R.id.faculty_name);
                EditText departmentNameEditText = dialog.findViewById(R.id.department_named_dbox);

                String facultyName = facultyNameEditText.getText().toString().trim();
                String departmentName = departmentNameEditText.getText().toString().trim();

                if (!facultyName.isEmpty() && !departmentName.isEmpty()) {
                    saveFacultyToFirestore(facultyName, departmentName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please enter faculty and department name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    private void saveFacultyToFirestore(String facultyName, String departmentName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facultyRef = db.collection("faculties");

        Map<String, Object> facultyData = new HashMap<>();
        facultyData.put("facultyName", facultyName);
        facultyData.put("departmentName", departmentName);

        facultyRef.add(facultyData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Faculty saved successfully
                        Toast.makeText(getContext(), "Faculty saved successfully", Toast.LENGTH_SHORT).show();
                        // Add the new faculty to the list and update the RecyclerView
                        FacultyModel faculty = new FacultyModel(documentReference.getId(), facultyName, departmentName);
                        facultyList.add(faculty);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save faculty
                        Toast.makeText(getContext(), "Failed to save faculty", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadFacultyData();
    }

    private void loadFacultyData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference facultyRef = db.collection("faculties");

        facultyRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String facultyId = document.getId();
                            String facultyName = document.getString("facultyName");
                            String departmentName = document.getString("departmentName");

                            FacultyModel faculty = new FacultyModel(facultyId, facultyName, departmentName);
                            facultyList.add(faculty);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load faculty data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}