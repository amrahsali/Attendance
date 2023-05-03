package com.example.attendance;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    // creating variables for our list, context, interface and position.
    private ArrayList<StudentModal> courseRVModalArrayList;
    private Context context;
    private CourseClickInterface courseClickInterface;
    int lastPos = -1;

    public StudentAdapter(ArrayList<StudentModal> courseRVModalArrayList, Context context) {
        this.courseRVModalArrayList = courseRVModalArrayList;
        this.context = context;
      //  this.courseClickInterface = courseClickInterface;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_record_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {

        StudentModal courseRVModal = courseRVModalArrayList.get(holder.getAdapterPosition());
        holder.Student_name.setText(courseRVModal.getStudentName());
        holder.Student_faculty.setText(courseRVModal.getStudentFaculty());
        holder.Student_department.setText( courseRVModal.getStudentDepartment());
        Uri uri = Uri.parse(courseRVModal.getProductImg());
        holder.Student_image.setImageURI(uri);
        Picasso.get().load(courseRVModal.getProductImg()).into(holder.Student_image);
        // adding animation to recycler view item on below line.
        setAnimation(holder.itemView, holder.getAdapterPosition());


    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // on below line we are setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }


    @Override
    public int getItemCount() {
        return courseRVModalArrayList.size();
    }

    public void startListening() {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our image view and text view on below line.
        private ImageView student_image;
        private TextView student_name, student_faculty, student_department, student_course;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initializing all our variables on below line.
            student_name = itemView.findViewById(R.id.student_name);
            student_image = itemView.findViewById(R.id.student_image);
            student_faculty = itemView.findViewById(R.id.student_faculty);
            student_department = itemView.findViewById(R.id.student_department);
            student_course = itemView.findViewById(R.id.student_course);
        }
    }
}
