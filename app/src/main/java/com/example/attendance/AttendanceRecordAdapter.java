package com.example.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AttendanceRecordAdapter extends FirebaseRecyclerAdapter<AttendanceRecord, AttendanceRecordAdapter.AttendanceRecordViewHolder> {

    public AttendanceRecordAdapter(@NonNull FirebaseRecyclerOptions<AttendanceRecord> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AttendanceRecordViewHolder holder, int position, @NonNull AttendanceRecord model) {
        holder.name.setText(model.getName1());
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
    }

    @NonNull
    @Override
    public AttendanceRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_cardview, parent, false);
        return new AttendanceRecordViewHolder(view);
    }

    static class AttendanceRecordViewHolder extends RecyclerView.ViewHolder {
        TextView name, title, date;

        public AttendanceRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.std_name);
            title = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
        }
    }
}
