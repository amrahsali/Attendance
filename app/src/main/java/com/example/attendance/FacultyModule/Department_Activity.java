package com.example.attendance.FacultyModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.attendance.R;

public class Department_Activity extends AppCompatActivity {


    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        linearLayout = findViewById(R.id.swe);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Department_Activity.this, Swe_levelActivity.class);
//                startActivity(intent);
                Toast.makeText(Department_Activity.this, "Item clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }
}