package com.example.attendance.AttendanceModule;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.attendance.LoginModule.MainActivity;
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
    private Context context;

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

      //  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gfgimage);

      //  scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
        // checking our permissions.
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        generatePDFbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling method to
                // generate our PDF file.
                generatePDF();
            }
        });
    }
    private void generatePDF() {
        // creating an object variable for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();
        int pageNumber = 1;

        // creating a variable for paint "title" used for adding text in our PDF file.
        Paint title = new Paint();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));

        // Iterate through your RecyclerView data and add it to the PDF document.
        for (int i = 0; i < adapter.getItemCount(); i++) {
            // Get the data item from your adapter
            AttendanceRecord item = adapter.getItem(i);

            // Start a new page for each item
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Draw the data from your item onto the canvas
            // Example: Assuming your data has name and description fields
            canvas.drawText("Name: " + item.getName1(), 50, 50, title);
            canvas.drawText("Description: " + item.getTitle(), 50, 80, title);

            // Finish the page
            pdfDocument.finishPage(page);
            pageNumber++;
        }

        // Define the file path and name
        File file = new File(Environment.getExternalStorageDirectory(), "RecyclerViewRecords.pdf");

        try {
            // Write the PDF document to the file
            pdfDocument.writeTo(new FileOutputStream(file));

            // Show a toast message on successful generation
            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle any exceptions
            e.printStackTrace();
        }

        // Close the PDF document
        pdfDocument.close();
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
