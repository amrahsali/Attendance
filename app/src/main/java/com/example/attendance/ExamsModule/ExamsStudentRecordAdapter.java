package com.example.attendance.ExamsModule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.AttendanceModule.AttendanceRecord;
import com.example.attendance.R;

import java.util.ArrayList;
import java.util.List;


public class ExamsStudentRecordAdapter extends RecyclerView.Adapter<ExamsStudentRecordAdapter.ExamsRecordViewHolder> {

    private Context context;
    private ArrayList<ExamsStudentRecordModal> examsModals;

    private List<ExamsStudentRecordModal> studentList;

//    public ExamsStudentRecordAdapter(List<ExamsStudentRecordModal> studentList) {
//        this.studentList = studentList;
//    }

    @NonNull
    @Override
    public ExamsRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exams_student_record_cardview, parent, false);
        return new ExamsRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsRecordViewHolder holder, int position) {
        ExamsStudentRecordModal student = studentList.get(position);
        holder.bind(student);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


    public ExamsStudentRecordAdapter(ArrayList<ExamsStudentRecordModal> examsModals, Context context) {
        this.examsModals = examsModals;
        this.context = context;
    }

    public static class ExamsRecordViewHolder extends RecyclerView.ViewHolder {
        private TextView studentNameTextView;
        private TextView studentMatNoTextView;

        public ExamsRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.record_student_name);
            studentMatNoTextView = itemView.findViewById(R.id.record_student_mat_no);
        }

        public void bind(ExamsStudentRecordModal student) {
            studentNameTextView.setText(student.getStudentName());
            studentMatNoTextView.setText(student.getStudentMatricNo());
        }
        }


    }
