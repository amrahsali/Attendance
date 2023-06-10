package com.example.attendance.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.attendance.R;

import java.util.List;

// Create a custom adapter class for the spinner
public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;

    public CustomSpinnerAdapter(Context context, List<String> items) {
        super(context, R.layout.spinner_item, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use a custom layout for the selected item
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Use the same custom layout for dropdown items
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return view;
    }
}

