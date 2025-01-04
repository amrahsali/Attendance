package com.example.attendance.StaffModule;

import static android.content.Context.MODE_PRIVATE;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.StudentModule.StudentModal;
import com.example.attendance.Utility.LocalStorageUtil;
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
import com.google.firebase.database.ValueEventListener;
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
        loadStaffFromLocalStorage();


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

        fab.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(getActivity(), StaffAddition.class);
            startActivity(intent1);
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Stop the refreshing animation
                    refreshData();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }, 100); // 10 seconds delay

        });

        return view;
    }

    private void refreshData() {
        // Clear the existing data
        staffRVModalArrayList.clear();
        staffAdapter.notifyDataSetChanged();
        fetchAndUpdateData();
    }

    private void fetchAndUpdateData() {
        DatabaseReference staffRef = FirebaseDatabase.getInstance().getReference("Staff");
        staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<StaffRVModal> staffList = new ArrayList<>();
                for (DataSnapshot staffSnapshot : dataSnapshot.getChildren()) {
                    StaffRVModal staff = staffSnapshot.getValue(StaffRVModal.class);
                    if (staff != null) {
                        staffList.add(staff);
                    }
                }
                updateLocalStorage(staffList);
                loadStaffFromLocalStorage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch student data: " + databaseError.getMessage());
            }
        });
    }

    private void updateLocalStorage(List<StaffRVModal> staffList) {
        String staffDataJson = new Gson().toJson(staffList);
        LocalStorageUtil.saveAndApply(staffDataJson, "staff_data", "system_staff_data", getActivity() );
    }

    private void loadStaffFromLocalStorage() {
        List<StaffRVModal> staffs = LocalStorageUtil.retrieveStaffDataFromLocalStorage(getContext());

        if (staffs != null && !staffs.isEmpty()) {
            staffRVModalArrayList.clear();
            staffRVModalArrayList.addAll(staffs);
            Log.d("Staffs Fragment", "loaded values from localStorage");
            staffAdapter.notifyDataSetChanged();
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No Staff Found");
        }
    }




}