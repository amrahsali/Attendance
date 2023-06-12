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
        holder.txtDepartmentName.setText(faculty.getDept().size() + " Departments");
    }

    @Override
    public int getItemCount() {
        return facultyList.size();
    }

    public FacultyModel getItem(int position) {
        if (position >= 0 && position < facultyList.size()) {
            return facultyList.get(position);
        } else {
            return null; // or handle the out-of-bounds case accordingly
        }    }

    public FacultyModel removeItem(int position) {
     return null;

    }

    public void updateItem(FacultyModel updatedItem) {
        int position = getItemPosition(updatedItem);
        if (position != RecyclerView.NO_POSITION) {
            // Update the item at the specified position
            facultyList.set(position, updatedItem);
            notifyItemChanged(position);
        }
    }
    private int getItemPosition(FacultyModel item) {
        for (int i = 0; i < facultyList.size(); i++) {
            if (facultyList.get(i).getId().equals(item.getId())) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
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
