package com.example.attendance.ExamsModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.AttendanceModule.AttendanceRecord;
import com.example.attendance.ExamsModule.ExamsAdapter;

//import com.example.attendance.FacultyModule.CourseAdapter;
import com.example.attendance.FacultyModule.CourseAdapter;
import com.example.attendance.R;
import com.example.attendance.StudentModule.StudentAddition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExaminationFragment extends Fragment {

    RecyclerView examsRV;
    ArrayList<ExamsModal> examsModalsArrayList;
    ExamsAdapter examsAdapter;
    DatabaseReference examsRef;

    Spinner coursesSpinner, staffsSpinner;
    private Button examsTime;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormat;
    FirebaseAuth mAuth;
    ImageButton selectStaff;
    private ArrayAdapter<String> coursesAdapter, staffsAdapter;

    private ArrayList<String> staffsList;

    private LinearLayout staffListLayout;


    private Context context;



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
        staffsList = new ArrayList<>();
        examsAdapter = new ExamsAdapter(examsModalsArrayList, getContext());
        coursesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        staffsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staffsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
//        final EditText examNameEditText = dialogView.findViewById(R.id.edt_nameExam);
        examsTime = dialogView.findViewById(R.id.examsTime);
        coursesSpinner = dialogView.findViewById(R.id.course_spinner);
        staffsSpinner = dialogView.findViewById(R.id.staff_spinner);
        staffListLayout = dialogView.findViewById(R.id.staff_list_layout);
        selectStaff = dialogView.findViewById(R.id.select_staff_btn);
        calendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
        loadCoursesData();
        loadStaffData();
        coursesSpinner.setAdapter(coursesAdapter);
        staffsSpinner.setAdapter(staffsAdapter);

        selectStaff.setOnClickListener(v1 -> {
            String staffName = staffsSpinner.getSelectedItem().toString();
            if (!staffName.equals("select invigilator") && !staffsList.contains(staffName)) {
                staffsList.add(staffName);
                addStaffToLayout(staffName);
            }
        });


        examsTime.setOnClickListener(v -> showDateTimePickerDialog());
        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String examName = coursesSpinner.getSelectedItem().toString();
            String dateFormat = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            String examTime = simpleDateFormat.format(calendar.getTime());
            saveExamToFirebase(examName, dateTimeFormat.format(calendar.getTime()));
        });
        builder.show();
    }

    private void saveExamToFirebase(String departmentName, String examsTIme) {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference().child("exams");

        String examId = examsRef.push().getKey();
        if (examId != null) {
            ExamsModal exam = new ExamsModal(examId, departmentName, staffsList, examsTIme);
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

    private void showDatePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateExamsTime();
            }
        };

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(getContext(), timeSetListener, hour, minute, false).show();
    }


    private void showDateTimePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimePickerDialog();
        };

        new DatePickerDialog(getContext(), DatePickerDialog.THEME_HOLO_DARK, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateExamsDateTime();
        };

        new TimePickerDialog(getContext(), TimePickerDialog.THEME_HOLO_DARK, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void updateExamsDateTime() {
        examsTime.setText(dateTimeFormat.format(calendar.getTime()));
    }




    private void updateExamsTime() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        examsTime.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void addStaffToLayout(String staffName) {
        TextView coursesTextView = new TextView(getContext());
        coursesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        coursesTextView.setText(staffName);
        coursesTextView.setTextColor(Color.BLACK);
        coursesTextView.setTextSize(16);
        staffListLayout.addView(coursesTextView);
    }

    private void loadCoursesData(){
        coursesAdapter.add("Select course");
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> courseNames = new ArrayList<>();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseName = courseSnapshot.child("courseName").getValue(String.class);
                    courseNames.add(courseName);
                }
                coursesAdapter.addAll(courseNames);
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load course data: " + databaseError.getMessage());
            }
        });
    }

    private void loadStaffData(){
        staffsAdapter.add("Select Invigilator");
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("Staff");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> staffs = new ArrayList<>();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String staff = courseSnapshot.child("productName").getValue(String.class);
                    staffs.add(staff);
                }
                staffsAdapter.addAll(staffs);
                staffsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load courses data: " + databaseError.getMessage());
            }
        });
    }

}