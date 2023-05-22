package com.example.attendance.FacultyModule.StaffModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendance.R;
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
    private ProgressBar loadingPB;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private RecyclerView staffRV;
    private StaffAdapter staffAdapter;
    private ArrayList<StaffRVModal> staffRVModalArrayList;
    Dialog dialog;




    public StaffListFragment() {
        // Required empty public constructor
    }


    


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();

        View view =  inflater.inflate(R.layout.fragment_staff_list, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        dialog = new Dialog(getContext());
        databaseReference = firebaseDatabase.getReference("Staff");
        FloatingActionButton fab = view.findViewById(R.id.idFABAdd);
        staffRVModalArrayList = new ArrayList<>();
        loadingPB = view.findViewById(R.id.idPBLoading);
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        staffAdapter = new StaffAdapter(staffRVModalArrayList, getContext());
        staffRV = view.findViewById(R.id.idRVCourses);
        staffRV.setLayoutManager(new LinearLayoutManager(getContext()));
        staffRV.setHasFixedSize(true);
        staffRV.setAdapter(staffAdapter);
        getStaff();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // on recycler view item swiped then we are deleting the item of our recycler view.
                if(direction==ItemTouchHelper.LEFT){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you want to delete?")
                            .setPositiveButton("confirm", (dialogInterface, i1) -> {
                                //viewmodal.delete(staffAdapter.get.getCourseAt(viewHolder.getAdapterPosition()));
                                staffAdapter.notifyDataSetChanged();
                                //databaseReference.child(staffAdapter.getItemId(viewHolder.getAdapterPosition())).removeValue();
                                Toast.makeText(getContext(), "Staff deleted deleted", Toast.LENGTH_SHORT).show();
                            }).setNegativeButton("cancel", (dialogInterface, i12) -> {
                                staffAdapter.notifyDataSetChanged();
                                builder.create().cancel();
                            });
                    builder.create().show();
                }
            }
        }). attachToRecyclerView(staffRV);



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