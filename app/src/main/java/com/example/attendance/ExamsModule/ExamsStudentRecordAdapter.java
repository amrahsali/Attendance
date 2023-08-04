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


public class ExamsStudentRecordAdapter extends RecyclerView.Adapter<ExamsStudentRecordAdapter.ExamsViewHolder> {

    private Context context;
    private ArrayList<ExamsStudentRecordModal> examsModals;

    public ExamsStudentRecordAdapter(ArrayList<ExamsStudentRecordModal> examsModals, Context context) {
        this.examsModals = examsModals;
        this.context = context;
    }

    @NonNull
    @Override
    public ExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exams_student_record_cardview, parent, false);
        return new ExamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsViewHolder holder, int position) {
        ExamsStudentRecordModal exam = examsModals.get(position);
       // holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

//    @Override
//    public int getItemCount() {
//        return examsModals.size();
//    }

    public AttendanceRecord getItem(int i) {
        return null;
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameView, matricNoView;

        public ExamsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.record_student_name);
            matricNoView = itemView.findViewById(R.id.record_student_mat_no);
        }

        public void bind(ExamsStudentRecordModal exam) {
            nameView.setText(exam.getStudentName());
            matricNoView.setText(exam.getStudentMatricNo());
        }



//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            if (position != RecyclerView.NO_POSITION) {
//                ExamsModal exam = examsModals.get(position);
//                openAnotherActivity(exam);
//            }
//        }

    }
}
