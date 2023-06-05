package com.example.attendance.FacultyModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.util.ArrayList;
import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<FacultyAdapter.ViewHolder> {

    int lastPos = -1;

    private Context context;
    private List<FacultyModel> facultyList;

    public FacultyAdapter(Context context, ArrayList<FacultyModel> facultyList) {
        this.context = context;
        this.facultyList = facultyList;
    }


    public FacultyAdapter(ArrayList<FacultyModel> facultyList) {
        this.facultyList = facultyList;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_cardview, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacultyModel faculty = facultyList.get(position);
        holder.txtFacultyName.setText(faculty.getName());
        holder.txtDepartmentName.setText(faculty.getDept().size());
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFacultyName;
        TextView txtDepartmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFacultyName = itemView.findViewById(R.id.faculty_name);
            txtDepartmentName = itemView.findViewById(R.id.department_name);
        }
    }

}
