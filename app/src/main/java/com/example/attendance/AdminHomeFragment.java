package com.example.attendance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AdminHomeFragment extends Fragment {

    Dialog dialog;
    Button button;
    TextView nameTextView, department, faculty, course;


    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);


        button = view.findViewById(R.id.updtProfile_btn);

        nameTextView = view.findViewById(R.id.username);
        department = view.findViewById(R.id.userdepartment);
        faculty = view.findViewById(R.id.userfaculty);
        course = view.findViewById(R.id.usercourse);


        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_dialog_box2);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        EditText nameEditText = dialog.findViewById(R.id.edt_name);
        EditText facultyEditText = dialog.findViewById(R.id.edt_faculty);
        EditText departmentEditText = dialog.findViewById(R.id.edt_department);
        EditText courseEditText = dialog.findViewById(R.id.edt_course);

        Button saveButton = dialog.findViewById(R.id.add_course_save);

        saveButton.setOnClickListener((View v) -> {
                    String name = nameEditText.getText().toString();
                    String facty = facultyEditText.getText().toString();
                    String dptment = departmentEditText.getText().toString();
                    String courses = courseEditText.getText().toString();


            nameTextView.setText(name);
            department.setText(dptment);
            faculty.setText(facty);
            course.setText(courses);





            dialog.dismiss();
        });


        button.setOnClickListener((View v) -> {
            dialog.show();
        });

        return view;


    }
}