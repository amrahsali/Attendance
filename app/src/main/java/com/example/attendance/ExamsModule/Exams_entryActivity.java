package com.example.attendance.ExamsModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.attendance.R;
import com.example.attendance.ScanActivity;

public class Exams_entryActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_entry);

        button = findViewById(R.id.savebtnExams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Exams_entryActivity.this, ScanActivity.class);
                intent.putExtra("origin","exams");

                startActivity(intent);
            }
        });

    }
}