package com.example.attendance.Utility;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.attendance.FacultyModule.FacultyModel;
import com.example.attendance.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class FacultyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private EditText departmentEditText;
    private LinearLayout departmentListLayout;
    private ArrayList<String> departmentList;
    private Button saveButton;
    private FacultyDialogListener dialogListener;

    private SaveFacultyListener saveFacultyListener;

    public static FacultyBottomSheetDialogFragment newInstance(FacultyModel item) {
        FacultyBottomSheetDialogFragment fragment = new FacultyBottomSheetDialogFragment();
        Bundle args = new Bundle();
        // Pass the necessary data to the dialog fragment through arguments
        args.putSerializable("faculty_item", item);
        fragment.setArguments(args);
        return fragment;
    }



    //public void setFacultyDialogListener(FacultyDialogListener listener) {
      //  this.dialogListener = listener;
    //}

    public void setSaveFacultyListener(SaveFacultyListener listener) {
        this.saveFacultyListener = listener;
    }

    public interface FacultyDialogListener {
        void onSaveClicked(String facultyName, ArrayList<String> departmentNames);
    }

    public interface SaveFacultyListener {
        void onSaveFaculty(String facultyName, ArrayList<String> departmentList);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (FacultyDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement FacultyDialogListener");
        }
    }

    public FacultyBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_faculty_dialogbox, container, false);

        departmentEditText = view.findViewById(R.id.department_named_dbox);
        departmentListLayout = view.findViewById(R.id.department_list_layout);
        ImageButton addDepartmentButton = view.findViewById(R.id.add_department_button);
        saveButton = view.findViewById(R.id.add_faculty_save);

        departmentList = new ArrayList<>();

        addDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departmentName = departmentEditText.getText().toString().trim();
                if (!departmentName.isEmpty()) {
                    departmentList.add(departmentName);
                    departmentEditText.setText("");
                    addDepartmentToLayout(departmentName);
                }
            }
        });

        saveButton.setOnClickListener(v -> {
            EditText facultyNameEditText = view.findViewById(R.id.faculty_name);
            String facultyName = facultyNameEditText.getText().toString().trim();
            if (!facultyName.isEmpty() && !departmentList.isEmpty()) {
                saveFacultyListener.onSaveFaculty(facultyName, departmentList);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter faculty and department name", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

    private void addDepartmentToLayout(String departmentName) {
        TextView departmentTextView = new TextView(requireContext());
        departmentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        departmentTextView.setText(departmentName);
        departmentTextView.setTextColor(Color.BLACK);
        departmentTextView.setTextSize(16);
        departmentListLayout.addView(departmentTextView);
    }
}

