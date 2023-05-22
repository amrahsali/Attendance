package com.example.attendance.AttendanceModule;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.attendance.ExamsModule.Exams_record_Activity;
import com.example.attendance.R;


public class RecordFragment extends Fragment {
    Button button;
    Button button2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

            button = view.findViewById(R.id.attendance_btn);
            button2 = view.findViewById(R.id.exams_btn );

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Attendance_record_Activity.class);
                    startActivity(i);
                }
            });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Exams_record_Activity.class);
                startActivity(i);
            }
        });

        return view;

    }

}

