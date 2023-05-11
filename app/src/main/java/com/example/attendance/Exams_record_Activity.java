package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Exams_record_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExamsRecordAdapter adapter; // Create an object of the Adapter class
    private DatabaseReference mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_record);

        mbase = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.rvExamsRecord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ExamsRecord> options =
                new FirebaseRecyclerOptions.Builder<ExamsRecord>()
                        .setQuery(mbase, ExamsRecord.class)
                        .build();


        adapter = new ExamsRecordAdapter(options);
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