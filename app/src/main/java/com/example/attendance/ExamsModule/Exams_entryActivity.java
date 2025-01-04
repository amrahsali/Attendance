package com.example.attendance.ExamsModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.AttendanceModule.Attendance_record_Activity;
import com.example.attendance.BuildConfig;
import com.example.attendance.R;
import com.example.attendance.Utility.LocalStorageUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Exams_entryActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    Button button;
    TextView examsTitle, examsTime;
    LinearLayout invigilatorList;
    private ArrayList<String> invList;

    ArrayList<ExamsStudentRecordModal> examsModalsArrayList;
    String examsName = "";
    String time = "";
    String examsEndTime = "";

    RecyclerView examsRV;

    DatabaseReference examsRecordRef;

    ExamsStudentRecordAdapter examsRecordAdapter;

    Calendar currentDateTime = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_entry);
        examsTitle = findViewById(R.id.exams_name);
        examsTime = findViewById(R.id.exams_time);
        invList = new ArrayList<>();
        invigilatorList = findViewById(R.id.invigilator_list_layout);
        examsModalsArrayList = new ArrayList<>();
        examsRV = findViewById(R.id.examsRecordlist);
        examsRecordAdapter = new ExamsStudentRecordAdapter(examsModalsArrayList, this);

        examsRV.setLayoutManager(new LinearLayoutManager(this));
        examsRV.setAdapter(examsRecordAdapter);

        // Retrieve the values from the intent
        Intent intent = getIntent();
        examsName = intent.getStringExtra("ExamsName");
        //String invigilator = intent.getStringExtra("invigilator");
        time = intent.getStringExtra("time");
        examsEndTime = intent.getStringExtra("examsEndTime");

        Calendar currentDateTime = Calendar.getInstance();

        loadStaffData();

        Date examsEndDateTime = parseDateTime(examsEndTime);

        button = findViewById(R.id.savebtnExams);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        if (examsEndDateTime != null) {
            if (examsEndDateTime.before(currentDateTime.getTime())) {
                button.setText(R.string.download_records);
            }
        }

        button.setOnClickListener(v -> {
            if (examsEndDateTime.before(currentDateTime.getTime())) {
                // Generate exams record and make a query to Firebase
                generateExamsRecordAndQueryFirebase(examsName, time);
            } else {
                // If it's not yet 3 hours after the exams datetime, simply start the ExamsScanActivity
                Intent intent1 = new Intent(Exams_entryActivity.this, ExamsScanActivity.class);
                intent1.putExtra("ExamsName", examsName);
                intent1.putExtra("ExamsTime", time);
                startActivity(intent1);
            }
        });

        loadExamsRecordData();
    }

    private boolean checkPermission() {
        int resultRead = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int resultWrite = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return resultRead == PackageManager.PERMISSION_GRANTED && resultWrite == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void addStaffToLayout(String staffName) {
        TextView coursesTextView = new TextView(this);
        coursesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        coursesTextView.setText(staffName);
        coursesTextView.setTextColor(Color.BLACK);
        coursesTextView.setTextSize(20);
        invigilatorList.addView(coursesTextView);
    }

    private void loadStaffData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("exams");
        Query query = examsRef.orderByChild("courseName").equalTo(examsName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // ArrayList<String> invList = new ArrayList<>();
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String examId = examSnapshot.child("examId").getValue(String.class);
                    String courseName = examSnapshot.child("courseName").getValue(String.class);
                    String time = examSnapshot.child("time").getValue(String.class);
                    DataSnapshot invigilatorsSnapshot = examSnapshot.child("invigilators");
                    ArrayList<String> invigilators = new ArrayList<>();
                    for (DataSnapshot invigilatorSnapshot : invigilatorsSnapshot.getChildren()) {
                        String invigilator = invigilatorSnapshot.getValue(String.class);
                        invigilators.add(invigilator);
                        addStaffToLayout(invigilator);
                    }
                    invList.addAll(invigilators);
                    examsTitle.setText(courseName);
                    examsTime.setText(time);
                }
                // Do something with the invList ArrayList
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });
    }

    private Date parseDateTime(String dateTimeString) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
        try {
            return format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void generateExamsRecordAndQueryFirebase(String examsName, String time) {
        // Fetch student data from Firebase
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("ExamsRecord");

        Query query = examsRef.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ExamsStudentRecordModal> examsStudentRecordModals = new ArrayList<>();

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseKey = courseSnapshot.getKey();
                    if (courseKey.startsWith(examsName + "-" + null)) {
                        for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            String studentName = studentSnapshot.child("studentName").getValue(String.class);
                            String studentMatNum = studentSnapshot.child("matNumber").getValue(String.class);
                            examsStudentRecordModals.add(new ExamsStudentRecordModal(studentName, studentMatNum));
                        }
                    }
                }

                String examsRecordDataJson = new Gson().toJson(examsStudentRecordModals);
                LocalStorageUtil.saveAndApply(examsRecordDataJson, "exam_data", "system_exam_data", Exams_entryActivity.this);

                // Call the PDF generation method here
                generatePDF(examsStudentRecordModals, examsName, time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });
    }

    private void generateExamsRecordAndUseLocalData(String examsName, String time) {
        ArrayList<ExamsStudentRecordModal> examsStudentRecordModals = LocalStorageUtil.retrieveExamsRecordDataFromLocal(this, examsName);
        if (!examsStudentRecordModals.isEmpty()) {
            generatePDF(examsStudentRecordModals, examsName, time);
        } else {
            Toast.makeText(this, "No local data available to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void generatePDF(ArrayList<ExamsStudentRecordModal> examsStudentRecordModals, String examsName, String time) {
        // Generate a PDF document
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000, 1400, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(70);
        titlePaint.setColor(Color.BLACK);
        canvas.drawText(examsName + " Students Record", 500, 80, titlePaint);

        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);

        int yPos = 150;
        for (ExamsStudentRecordModal record : examsStudentRecordModals) {
            canvas.drawText("Name: " + record.getStudentName(), 50, yPos, paint);
            canvas.drawText("Matric Number: " + record.getStudentMatricNo(), 50, yPos + 40, paint);
            yPos += 80;
        }

        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), examsName + "_Students_Record.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF file saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Optionally, you can open the generated PDF
            openGeneratedPDF(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF file", Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }

    private void openGeneratedPDF(File file) {
        Uri pdfUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExamsRecordData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("ExamsRecord");

        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    // Split the key by "-" to extract the examName
                    String[] parts = key.split("-");
                    if (parts.length >= 2) {
                        String examName = parts[0];
                        if (examName.equals(examsName)) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String studentName = childSnapshot.child("studentName").getValue(String.class);
                                String matNumber = childSnapshot.child("matNumber").getValue(String.class);
                                ExamsStudentRecordModal modal = new ExamsStudentRecordModal(studentName, matNumber);
                                examsModalsArrayList.add(modal);
                            }
                        }
                    }
                }
                examsRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (readAccepted && writeAccepted) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    // Permission granted, do your work here
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // Permission denied, show a message to the user and handle the scenario appropriately
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted, do your work here
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    // Permission denied, show a message to the user and handle the scenario appropriately
                }
            }
        }
    }
}
