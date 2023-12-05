package com.example.attendance.ExamsModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

        if (examsEndDateTime != null){
            if (examsEndDateTime.before(currentDateTime.getTime())){
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
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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

    // Method to parse the datetime string into a Date object
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
        // Fetch student data from Firebase (if needed)
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("ExamsRecord");

        Query query = examsRef.orderByKey(); // Adjust the query as needed

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ExamsStudentRecordModal> studentList = new ArrayList<>();

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseKey = courseSnapshot.getKey();
                    if (courseKey.startsWith(examsName + "-" + null)) {
                        for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            String studentName = studentSnapshot.child("studentName").getValue(String.class);
                            String studentMatNum = studentSnapshot.child("matNumber").getValue(String.class);
                            studentList.add(new ExamsStudentRecordModal(studentName, studentMatNum));
                        }
                    }
                }

                // Call the PDF generation method here
                generatePDF(studentList, examsName, time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });
    }




    private void loadExamsRecordData() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("ExamsRecord");

        Query query = examsRef.orderByKey(); // Assuming you want to order by the keys (coursetitle+date)

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseKey = courseSnapshot.getKey();
                    Log.i(TAG, "onCreate: exams key is is: " + courseKey);
                    if (courseKey.startsWith(examsName + "-" + null)) {
                        for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            String studentName = studentSnapshot.child("studentName").getValue(String.class);
                            String studentMatNum = studentSnapshot.child("matNumber").getValue(String.class);
                            Log.i(TAG, "onCreate: exams time is: " + studentMatNum);

                            examsModalsArrayList.add(new ExamsStudentRecordModal(studentName, studentMatNum));
                           // examsModalsArrayList.add(new ExamsStudentRecordModal("amrah sali", "studentMatNum"));

                        }
                    }
                }



//                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
//                    String xcourseKey = courseSnapshot.getKey();
//                    Toast.makeText(Exams_entryActivity.this, "exams is: " + courseSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Exams_entryActivity.this, "exams is: " + examsName + "-" + null, Toast.LENGTH_SHORT).show();
//
//                    if (xcourseKey.startsWith(examsName + "-" + null)) {
//                        for (DataSnapshot studentRecordSnapshot : dataSnapshot.getChildren()) {
//                            String courseKey = courseSnapshot.getKey();
//                            if (courseKey.startsWith(desiredCourseName)) {
//                                for (DataSnapshot studentSnapshot : courseSnapshot.getChildren()) {
//                                    String studentId = studentSnapshot.getKey();
//                                    String studentName = studentSnapshot.child("student-name").getValue(String.class);
//
//                                    studentList.add(new Student(studentId, studentName));
//                                }
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load exams data: " + databaseError.getMessage());
            }
        });

    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }






    private void generatePDF(ArrayList<ExamsStudentRecordModal> studentList, String examsName, String time) {
        PdfDocument document = new PdfDocument();


        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        float x = 50, y = 50;

        canvas.drawText("Exams Name: " + examsName, x, y, paint);
        y += 20;
        canvas.drawText("Time: " + time, x, y, paint);

        y += 40;

        for (ExamsStudentRecordModal student : studentList) {
            canvas.drawText("Name: " + student.getStudentName(), x, y, paint);
            y += 20;
            canvas.drawText("Matric No: " + student.getStudentMatricNo(), x, y, paint);
            y += 30;
        }

        document.finishPage(page);

        //String filePath = getExternalFilesDir(null) + "/" + examsName + "_Record.pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + examsName + "_Record.pdf";

        File file = new File(filePath);

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF generated and saved: " + filePath, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, generate and save the PDF
//                generateExamsRecordAndQueryFirebase(examsName, time);
//            } else {
//                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


}