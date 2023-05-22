package com.example.attendance.FacultyModule;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.attendance.R;

public class FacultyFragment extends Fragment {

LinearLayout linearLayout;

    public FacultyFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_faculty, container, false);

      linearLayout =view.findViewById(R.id.faculty_layout);

      linearLayout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(getActivity(), Department_Activity.class);
              startActivity(i);
          }
      });
        return view;

    }
}