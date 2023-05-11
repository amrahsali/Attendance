package com.example.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ExamsRecordAdapter extends FirebaseRecyclerAdapter<ExamsRecord, ExamsRecordAdapter.ExamsRecordViewHolder> {

    public ExamsRecordAdapter(@NonNull FirebaseRecyclerOptions<ExamsRecord> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExamsRecordAdapter.ExamsRecordViewHolder holder, int position, @NonNull ExamsRecord model) {
        holder.nameExams.setText(model.getNameExams());
        holder.titleExams.setText(model.getTitleExams());
        holder.dateExams.setText(model.getDateExams());
    }

    @NonNull
    @Override
    public ExamsRecordAdapter.ExamsRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recordexams_cardview, parent, false);
        return new ExamsRecordViewHolder(view);
    }

    static class ExamsRecordViewHolder extends RecyclerView.ViewHolder {
        TextView nameExams, titleExams, dateExams;

        public ExamsRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExams = itemView.findViewById(R.id.examsrcd_name);
            titleExams = itemView.findViewById(R.id.examsrcd_title);
            dateExams = itemView.findViewById(R.id.examsrcd_date);
        }
    }
}
