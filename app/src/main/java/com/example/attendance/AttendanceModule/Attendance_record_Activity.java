package com.example.attendance.AttendanceModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.example.attendance.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Attendance_record_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceRecordAdapter adapter; // Create an object of the Adapter class
    private DatabaseReference mbase;

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
