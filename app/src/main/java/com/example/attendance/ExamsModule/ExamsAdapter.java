package com.example.attendance.ExamsModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Context context;
    private ArrayList<ExamsModal> examsModals;

    public ExamsAdapter(ArrayList<ExamsModal> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
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
        holder.bind(exam);
    }

    @Override
    public int getItemCount() {
        return examsModals.size();
    }

    public class ExamsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;

        public ExamsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.dapt_name);
        }

        public void bind(ExamsModal exam) {
            nameTextView.setText(exam.getDepartmentName());
        }
    }
}





