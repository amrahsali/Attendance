package com.example.attendance.ExamsModule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.util.ArrayList;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ExamsViewHolder> {

    private Context context;
    private ArrayList<ExamsModal> examsModals;

    int lastPos = -1;



    public ExamsAdapter(ArrayList<ExamsModal> examsModals, Context context) {
        this.examsModals = examsModals == null && this.examsModals.isEmpty()  ? new ArrayList<>() : examsModals;
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
        if (!examsModals.isEmpty()) {
            int reversePosition = getItemCount() - position - 1;

            ExamsModal exam = examsModals.get(holder.getAdapterPosition());
            if (exam != null) {
//                holder.invigilator.setText(exam.getInvigilators() != null && !exam.getInvigilators().isEmpty() ? exam.getInvigilators().get(0) : "");
                holder.nameTextView.setText(exam.getCourseName() != null ? exam.getCourseName() : "");
                holder.time.setText(exam.getTime() != null ? exam.getTime() : "");
            }

            setAnimation(holder.itemView, reversePosition);
        }
    }


    @Override
    public int getItemCount() {
        return examsModals == null || examsModals.isEmpty() ? 0 : examsModals.size();
    }


    public class ExamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTextView, invigilator, time;

        public ExamsViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.dapt_name);
            invigilator = itemView.findViewById(R.id.invigilator);
            time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && examsModals != null && !examsModals.isEmpty()) {
                ExamsModal exam = examsModals.get(position);
                openAnotherActivity(exam);
            }
        }

        private void openAnotherActivity(ExamsModal exam) {
            Intent intent = new Intent(context, Exams_entryActivity.class);
            intent.putExtra("examId", exam.getExamId());
            intent.putExtra("ExamsName", exam.getCourseName());
//            intent.putExtra("invigilator", exam.getInvigilators());
            intent.putExtra("time", exam.getTime());
            intent.putExtra("examsEndTime", exam.getEndTime());
            context.startActivity(intent);
        }
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // on below line we are setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }
}
