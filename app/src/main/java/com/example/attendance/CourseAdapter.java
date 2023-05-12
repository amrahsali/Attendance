package com.example.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter  extends ArrayAdapter<Coursemodal> {

    // constructor for our list view adapter.
    public CourseAdapter(@NonNull Context context, ArrayList<Coursemodal> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.course_cardview, parent, false);
        }




        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Coursemodal dataModal = getItem(position);

        // initializing our UI components of list view item.
        TextView nameTV = listitemView.findViewById(R.id.course_name);
        TextView courseIV = listitemView.findViewById(R.id.code_name);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        nameTV.setText(dataModal.getCourseName());
        courseIV.setText(dataModal.getCodeName());


        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Toast.makeText(getContext(), "Item clicked is : " + dataModal.getCourseName(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }
}







