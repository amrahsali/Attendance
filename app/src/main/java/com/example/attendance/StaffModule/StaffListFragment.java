package com.example.attendance.StaffModule;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.Utility.ScanActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class StaffListFragment extends Fragment {

    private FirebaseAuth mAuth;

    boolean hasData = false;
    private ProgressBar loadingPB;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private RecyclerView staffRV;

    private TextView emptyTextView;

    private Handler handler;
    private Runnable timeoutRunnable;

    // Declare a member variable for the ChildEventListener
    private ChildEventListener childEventListener;
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
        loadingPB = view.findViewById(R.id.loading_pb);
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        staffAdapter = new StaffAdapter(staffRVModalArrayList, getContext());
        staffRV = view.findViewById(R.id.idRVCourses);
        staffRV.setLayoutManager(new LinearLayoutManager(getContext()));
        staffRV.setHasFixedSize(true);
        staffRV.setAdapter(staffAdapter);
        emptyTextView = view.findViewById(R.id.empty_text_view);
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

        // Find the SwipeRefreshLayout in the layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Set the listener for the swipe-to-refresh action
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Stop the refreshing animation
                    refreshData();
                    swipeRefreshLayout.setRefreshing(false);
                    // Check if there is data available
                    if (hasData) {
                        emptyTextView.setVisibility(View.GONE);
                    } else {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No Staff Found");
                    }
                }
            }, 100); // 10 seconds delay

        });

        return view;
    }

    private void retrieveStaffDataFromLocalStorage() {

        ScanActivity scanActivity = new ScanActivity();

        List<StaffRVModal> staffList  = scanActivity.retrieveStaffDataFromLocalStorage();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("your_staff_pref_name", MODE_PRIVATE);
        String staffDataJson = sharedPreferences.getString("staff_data", "");

        if (!staffDataJson.isEmpty()) {
            // Convert the JSON string to a list of StaffRVModal objects using Gson
            staffRVModalArrayList = new Gson().fromJson(staffDataJson, new TypeToken<List<StaffRVModal>>() {}.getType());
            staffAdapter.notifyDataSetChanged();
        } else {
            // Handle the case where no data is found in local storage
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No Staff Found");
        }
    }

    private void getStaff() {
        handler = new Handler();
        //on below line clearing our list.
        loadingPB.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        staffRVModalArrayList.clear();
//        Query query = databaseReference.get();
        startTimeoutRunnable(databaseReference);
        //on below line we are calling add child event listener method to read the data.
        databaseReference.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //on below line we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.GONE);
                //adding snapshot to our array list on below line.
                staffRVModalArrayList.add(snapshot.getValue(StaffRVModal.class));
                //notifying our adapter that data has changed.
                staffAdapter.notifyDataSetChanged();

                hasData = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.
                loadingPB.setVisibility(View.GONE);

                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //notifying our adapter when child is removed.
                staffAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //notifying our adapter when child is moved.
                staffAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                stopTimeoutRunnable();
                loadingPB.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("Error: " + error.getMessage());
            }
        });
    }

    private void startTimeoutRunnable(Query query) {
        // Initialize the timeout runnable
        timeoutRunnable = () -> {
            // Hide the loading progress bar
            loadingPB.setVisibility(View.GONE);

            if (staffRVModalArrayList.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("No Staff Found");
            }

            // Remove the child event listener
            databaseReference.removeEventListener(childEventListener);
        };

        // Schedule the runnable after 10 seconds
        handler.postDelayed(timeoutRunnable, 10000);
    }

    // Method to stop the timeout runnable
    private void stopTimeoutRunnable() {
        // Remove the timeout runnable callbacks
        handler.removeCallbacks(timeoutRunnable);
    }

    private void refreshData() {
        // Clear the existing data
        staffRVModalArrayList.clear();
        staffAdapter.notifyDataSetChanged();

        // Call the method to fetch the updated data
        getStaff();
    }




}