package com.example.attendance.ExamsModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.attendance.FacultyModule.CourseAdapter;
import com.example.attendance.FacultyModule.CourseModal;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.example.attendance.Utility.LocalStorageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private Button examsTime, examsEndTime, generatePdfButton;
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormat;
    FirebaseAuth mAuth;
    ImageButton selectStaff;
    private ArrayAdapter<String> coursesAdapter, staffsAdapter;

    private ArrayList<String> staffsList;

    private LinearLayout staffListLayout;

    private Context context;


    private static final int PERMISSION_REQUEST_CODE = 1;

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

        //generatePdfButton = view.findViewById(R.id.idBtnGntPDFExam);


        examsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        examsRV.setAdapter(examsAdapter);

        examsRef = FirebaseDatabase.getInstance().getReference().child("exams");


        loadExamsFromLocalStorage();

        if (isUserLoggedIn()){
            view.findViewById(R.id.examsFABtn).setOnClickListener(v -> showDialogBox());
            view.findViewById(R.id.examsFABtn).setVisibility(View.VISIBLE);
        }else {
            view.findViewById(R.id.examsFABtn).setVisibility(View.GONE);
        }
        context = getActivity();

        return view;
    }

    private void generatePdf() {
            // Create a new document
                Document document = new Document();

                // Get the path to the external storage directory
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myapp";

                // Create the directory if it doesn't exist
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // Create a new file for the PDF
                File file = new File(dir, "my_pdf.pdf");

                try {
                    // Create a PdfWriter instance to write the document to the file
                    PdfWriter.getInstance(document, new FileOutputStream(file));

                    // Open the document
                    document.open();

                    // Add paragraphs with data from the RecyclerView
                    String[] data = new String[0];
                    for (String item : data) {
                        document.add(new Paragraph(item));
                    }

                    // Close the document
                    document.close();

                    Log.d("PdfGenerator", "PDF generated successfully!");
                    Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "PDF generation failed!", Toast.LENGTH_SHORT).show();
                }
            }


//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0) {
//
//                // after requesting permissions we are showing
//                // users a toast message of permission granted.
//                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//
//                if (writeStorage && readStorage) {
//                    Toast.makeText(getActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Toast.makeText(getActivity(), "Permission Denied.", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        }
//    }

    private void finish() {
    }


    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Exam");
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_dialogbox_examsadd, null);
//        final EditText examNameEditText = dialogView.findViewById(R.id.edt_nameExam);
        examsTime = dialogView.findViewById(R.id.examsTime);
        examsEndTime = dialogView.findViewById(R.id.examsEndTime);
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
        examsEndTime.setOnClickListener(v -> showDateTimePickerDialogEnd());
        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String examName = coursesSpinner.getSelectedItem().toString();
            String dateFormat = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            String examTime = simpleDateFormat.format(calendar.getTime());
            saveExamToFirebase(examName, dateTimeFormat.format(calendar.getTime()), dateTimeFormat.format(calendar.getTime()));
        });
        builder.show();
    }

    private void saveExamToFirebase(String departmentName, String examsTIme, String examsEndTime) {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference().child("exams");

        String examId = examsRef.push().getKey();
        if (examId != null) {
            ExamsModal exam = new ExamsModal(examId, departmentName, staffsList, examsTIme,  examsEndTime);
            examsRef.child(departmentName).setValue(exam).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Exam added successfully", Toast.LENGTH_SHORT).show();
                            LocalStorageUtil.updateLastUpdatedTimestamp();
                            updateLocalStorageAfterExamAdded();
                            loadExamsFromLocalStorage();
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

    private void showDateTimePickerDialogEnd() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimePickerDialogEnd();
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

    private void showTimePickerDialogEnd() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            examsEndTime.setText(dateTimeFormat.format(calendar.getTime()));
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

   private void loadCoursesData() {
        coursesAdapter.clear();
        coursesAdapter.add("Select course");

        List<CourseModal> courses = LocalStorageUtil.retrieveCourseDataFromLocalStorage(getContext());
        if (courses != null) {
            for (CourseModal course : courses) {
                coursesAdapter.add(course.getCourseName());
            }
        } else {
            Log.e(TAG, "No course data found in local storage");
        }

        coursesAdapter.notifyDataSetChanged();
    }

    private void loadStaffData() {
        staffsAdapter.clear();
        staffsAdapter.add("Select Invigilator");

        List<StaffRVModal> staffList = LocalStorageUtil.retrieveStaffDataFromLocalStorage(getContext());
        if (staffList != null) {
            for (StaffRVModal staff : staffList) {
                staffsAdapter.add(staff.getProductName());
            }
        } else {
            Toast.makeText(getActivity(), "No staffs", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No staff data found in local storage");
        }

        staffsAdapter.notifyDataSetChanged();
    }


    public void showDatePickerDialog(View view) {
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

    private void loadExamsFromLocalStorage() {
        List<ExamsModal> exams = LocalStorageUtil.retrieveExamDataFromLocalStorage(getContext());
        if (exams != null && !exams.isEmpty()) {
            examsModalsArrayList.clear();
            examsModalsArrayList.addAll(exams);
            examsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "No exams data found in Local Storage", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateLocalStorageAfterExamAdded() {
        examsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ExamsModal> examList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ExamsModal exam = snapshot.getValue(ExamsModal.class);
                    if (exam != null) {
                        examList.add(exam);
                    }
                }
                String examsDataJson = new Gson().toJson(examList);
                LocalStorageUtil.saveAndApply(examsDataJson, "exam_data", "system_exam_data", getActivity() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });
    }

}