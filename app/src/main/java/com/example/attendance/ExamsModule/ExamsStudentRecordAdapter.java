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
    private ArrayList<ExamsModal> examsModals;

    public ExamsStudentRecordAdapter(ArrayList<ExamsModal> examsModals, Context context) {
        this.examsModals = examsModals;
        this.context = context;
    }

    @NonNull
    @Override
    public ExamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exams_cardview, parent, false);
        return new ExamsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsViewHolder holder, int position) {
        ExamsModal exam = examsModals.get(position);
       // holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return examsModals.size();
    }

    public AttendanceRecord getItem(int i) {
        return null;
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        private TextView nameTextView, invigilator, time;

        public ExamsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.dapt_name);
            invigilator = itemView.findViewById(R.id.invigilator);
            time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);

        }

        public void bind(ExamsModal exam) {
            nameTextView.setText(exam.getCourseName());
            invigilator.setText(exam.getInvigilators().get(0));
            time.setText(exam.getTime());
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ExamsModal exam = examsModals.get(position);
                openAnotherActivity(exam);
            }
        }

        private void openAnotherActivity(ExamsModal exam) {
            Intent intent = new Intent(context, Exams_entryActivity.class);
            intent.putExtra("examId", exam.getExamId());
            intent.putExtra("ExamsName", exam.getCourseName());
            intent.putExtra("invigilator", exam.getInvigilators());
            intent.putExtra("time", exam.getTime());
            intent.putExtra("examsEndTime", exam.getEndTime());
            context.startActivity(intent);
        }
    }
}
