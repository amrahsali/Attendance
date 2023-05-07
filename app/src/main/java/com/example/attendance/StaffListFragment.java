package com.example.attendance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class StaffListFragment extends Fragment {

    private FirebaseAuth mAuth;
    Button  create_staff;
    EditText username, phoneNumber, emailad, department1, faculty1;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;
    private static final int ADD_COURSE_REQUEST = 1;
    private RecyclerView coursesRV;
    private StaffRVModal staffRVModal;
    private StaffAdapter staffAdapter;
    private ArrayList<StaffRVModal> staffRVModalArrayList;




    public StaffListFragment() {
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
        mAuth = FirebaseAuth.getInstance();

        View view =  inflater.inflate(R.layout.fragment_staff_list, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Staff");
        FloatingActionButton fab = view.findViewById(R.id.idFABAdd);
        staffRVModalArrayList = new ArrayList<>();
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        staffAdapter = new StaffAdapter(staffRVModalArrayList, getContext());
        coursesRV = view.findViewById(R.id.idRVCourses);
        coursesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRV.setHasFixedSize(true);
        coursesRV.setAdapter(staffAdapter);
        getStaff();


        // adding on click listener for floating action button.
        fab.setOnClickListener((View v) -> {
            // starting a new activity for adding a new course
            // and passing a constant value in it.
            Intent intent1 = new Intent(getActivity(), StaffAddition.class);
            startActivity(intent1);


        });

        return view;
    }

    private void getStaff() {
        //on below line clearing our list.
        staffRVModalArrayList.clear();
        Query query = databaseReference.orderByChild("userID").equalTo(FirebaseAuth.getInstance().getUid());
        //on below line we are calling add child event listener method to read the data.
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //on below line we are hiding our progress bar.
                //loadingPB.setVisibility(View.GONE);
                //adding snapshot to our array list on below line.
                staffRVModalArrayList.add(snapshot.getValue(StaffRVModal.class));
                //notifying our adapter that data has changed.
                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.
                //loadingPB.setVisibility(View.GONE);
                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying our adapter when child is removed.
                staffAdapter.notifyDataSetChanged();
                //loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //notifying our adapter when child is moved.
                staffAdapter.notifyDataSetChanged();
                //loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}