package com.example.attendance.Utility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.LoginModule.MainActivity;
import com.example.attendance.R;

public class ScanActivity extends AppCompatActivity {
    ImageView print;
    TextView status;
    int click = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        print = findViewById(R.id.print_image);
        status = findViewById(R.id.status);


        print.setOnClickListener(v -> {
            if (click >= 2) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                String origin = null;
                if (bundle != null) {
                    origin = bundle.getString("origin");
                    if (origin != null && origin.equals("staffProfile")) {
                        //student attendance
                        Toast.makeText(this, "Attendance taken", Toast.LENGTH_SHORT).show();
                    }
                    if (origin != null && origin.equals("login")) {
                        //staff login
                        Intent i = new Intent(ScanActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    if (origin != null && origin.equals("exams")) {
                        Intent i = new Intent(ScanActivity.this, exams_dialogbox_Activity.class);
                        startActivity(i);
                    }
                }




            }else{
                print.setImageResource(R.drawable.printfailedxhdpi);
                status.setText("Try again");
                click ++;
            }

        });
    }
}