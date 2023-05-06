package com.example.attendance;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class RecordFragment extends Fragment {

    TextView all_card, vegies, fruit, grain, tuber, student_name, are_you_a_farmer, search_here;
    ImageView student_img;
    Context context;
    private RecyclerView recyclerView;
    private FloatingActionButton addCourseFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<StudentModal> courseRVModalArrayList;
    //private GridRecyclerViewHolder courseRVAdapter;
    private StudentAdapter courseRVAdapter;
    ImageView to_notification;
    Button addToCart;
    String uid;
    private MenuItem menuItem;
    private SearchView searchView;
    MediaPlayer mMediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);


        uid = FirebaseAuth.getInstance().getUid();

        recyclerView = view.findViewById(R.id.rvNumbers);
        courseRVModalArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //on below line we are getting database reference.
        databaseReference = firebaseDatabase.getReference("student");


        addCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a new activity for adding a course.
                Intent i = new Intent(getContext(), Student_profileActivity.class);
                startActivity(i);
            }
        });
        courseRVAdapter = new StudentAdapter(courseRVModalArrayList, getContext());
        // Toast.makeText(context, courseRVModalArrayList.get(0).toString(), Toast.LENGTH_SHORT).show();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerView.setAdapter(new MyRecyclerViewAdapter(1234), this);
        recyclerView.setAdapter(courseRVAdapter);


        return view;

    }

}

