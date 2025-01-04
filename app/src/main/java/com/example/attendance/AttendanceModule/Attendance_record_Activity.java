package com.example.attendance.AttendanceModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.attendance.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Attendance_record_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceRecordAdapter adapter; // Create an object of the Adapter class
    private DatabaseReference mbase;

    Button generatePDFbtn;

    int pageHeight = 1120;
    int pagewidth = 792;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);

        mbase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.rvAttendanceRecord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<AttendanceRecord> options =
                new FirebaseRecyclerOptions.Builder<AttendanceRecord>()
                        .setQuery(mbase, AttendanceRecord.class)
                        .build();

        adapter = new AttendanceRecordAdapter(options);
        recyclerView.setAdapter(adapter);

        generatePDFbtn = findViewById(R.id.idBtnGeneratePDF);

        if (!checkPermission()) {
            requestPermission();
        }

        generatePDFbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    generatePDF();
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        int pageNumber = 1;

        Paint title = new Paint();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        for (int i = 0; i < adapter.getItemCount(); i++) {
            AttendanceRecord item = adapter.getItem(i);

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            canvas.drawText("Name: " + item.getName1(), 50, 50, title);
            canvas.drawText("Description: " + item.getTitle(), 50, 80, title);

            pdfDocument.finishPage(page);
            pageNumber++;
        }

        File file = new File(Environment.getExternalStorageDirectory(), "RecyclerViewRecords.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "File Permission Granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "File Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
