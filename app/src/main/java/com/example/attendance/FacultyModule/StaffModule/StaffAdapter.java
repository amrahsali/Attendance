package com.example.attendance.FacultyModule.StaffModule;

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
import com.example.attendance.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {
    // creating variables for our list, context, interface and position.
    private ArrayList<StaffRVModal> courseRVModalArrayList;
    private Context context;
    private CourseClickInterface courseClickInterface;
    int lastPos = -1;


    // creating a constructor.
    public StaffAdapter(ArrayList<StaffRVModal> courseRVModalArrayList, Context context) {
        this.courseRVModalArrayList = courseRVModalArrayList;
        this.context = context;
//        this.courseClickInterface = courseClickInterface;
    }

    @NonNull
    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout file on below line.
        View view = LayoutInflater.from(context).inflate(R.layout.staff_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffAdapter.ViewHolder holder, int position) {
        // setting data to our recycler view item on below line.
        StaffRVModal courseRVModal = courseRVModalArrayList.get(holder.getAdapterPosition());
        holder.product_name.setText(courseRVModal.getProductName());
        holder.Product_description.setText(courseRVModal.getDepartment());
        holder.Product_price.setText("N. " + courseRVModal.getFaculty());
        holder.email.setText(courseRVModal.getEmail());
        Uri uri = Uri.parse(courseRVModal.getProductImg());
        holder.product_image.setImageURI(uri);
        Picasso.get().load(courseRVModal.getProductImg()).into(holder.product_image);
        // adding animation to recycler view item on below line.
        setAnimation(holder.itemView, holder.getAdapterPosition());
//        holder.product_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                courseClickInterface.onCourseClick(holder.getAdapterPosition());
//            }
//        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our image view and text view on below line.
        private ImageView product_image;
        private TextView product_name, Product_price, Product_description, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing all our variables on below line.
            product_name = itemView.findViewById(R.id.product_name);
            product_image = itemView.findViewById(R.id.product_image);
            Product_description = itemView.findViewById(R.id.product_description);
            Product_price = itemView.findViewById(R.id.product_price);
            email = itemView.findViewById(R.id.staff_email);
        }
    }

    // creating a interface for on click
    public interface CourseClickInterface {
        void onCourseClick(int position);
    }
}