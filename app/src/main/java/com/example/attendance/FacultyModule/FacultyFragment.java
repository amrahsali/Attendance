package com.example.attendance.FacultyModule;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendance.R;
import com.example.attendance.Utility.FacultyBottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacultyFragment extends Fragment {

    Dialog dialog;

    private Handler handler;

    private ProgressBar loadingPB;
    RecyclerView recyclerView;
    FacultyAdapter adapter;

    boolean hasData = false;
    DatabaseReference facultyRef;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    private TextView emptyTextView;
    private Runnable timeoutRunnable;

    private ArrayList<FacultyModel> facultyModelArrayList;

    public FacultyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faculty, container, false);
        recyclerView = view.findViewById(R.id.idRVFacultyt);
        dialog = new Dialog(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        Context context = requireContext();
        facultyModelArrayList = new ArrayList<>();
        loadingPB = view.findViewById(R.id.loading_pb);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        adapter = new FacultyAdapter(context, facultyModelArrayList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        facultyRef = firebaseDatabase.getReference("Faculty");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.facultyFABtn);
        loadFacultyData();

        if (isUserLoggedIn()) {
            fab.setOnClickListener(v -> showFacultyDialog());
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }


        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Set the listener for the swipe-to-refresh action
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                // Stop the refreshing animation
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
                // Check if there is data available
                if (hasData) {
                    emptyTextView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No faculty Found");
                }
            }, 100); // 10 seconds delay

        });
//     below method is use to add swipe to delete method for item of recycler view.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    // Handle right swipe action
                    FacultyModel item = adapter.getItem(viewHolder.getAdapterPosition());

                    // Create and show the BottomSheetDialogFragment
                    FacultyBottomSheetDialogFragment bottomSheetDialogFragment = FacultyBottomSheetDialogFragment.newInstance(item);
                    bottomSheetDialogFragment.setSaveFacultyListener(new FacultyBottomSheetDialogFragment.SaveFacultyListener() {
                        @Override
                        public void onSaveFaculty(String facultyName, ArrayList<String> departmentList) {
                            // Update the faculty data
                            item.setName(facultyName);
                            item.setDept(departmentList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    bottomSheetDialogFragment.show(getChildFragmentManager(), "bottom_sheet_tag");

        } else if (direction == ItemTouchHelper.LEFT) {
                    // Handle left swipe action
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Do you want to delete?")
                            .setPositiveButton("Confirm", (dialogInterface, i) -> {
                                int position = viewHolder.getAdapterPosition();
                                FacultyModel item = adapter.getItem(position);
                                Toast.makeText(getContext(), "test "+ item.getId(), Toast.LENGTH_SHORT).show();

                                // Delete the item from the RecyclerView
                                adapter.removeItem(position);

                                if (item.getId() != null) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Faculty");
                                    //  Log.i(TAG,"item is is:"+ item.getId());
                                    databaseReference.child(item.getId()).removeValue()
                                            .addOnSuccessListener(aVoid -> {
                                                // Item deleted successfully
                                                Toast.makeText(getContext(), "Course deleted", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Failed to delete the item
                                                Toast.makeText(getContext(), "Failed to delete course", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Handle the case where the item's ID is null
                                    Toast.makeText(getContext(), "Invalid item ID", Toast.LENGTH_SHORT).show();
                                }

                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                // User canceled the delete operation, notify the adapter to update the view
                                adapter.notifyDataSetChanged();
                                builder.create().cancel();
                            });
                    builder.create().show();
                }
            }



        }).attachToRecyclerView(recyclerView);

        return view;

    }



    private void showFacultyDialog() {
        FacultyBottomSheetDialogFragment bottomSheetDialogFragment = new FacultyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setSaveFacultyListener(new FacultyBottomSheetDialogFragment.SaveFacultyListener() {
            @Override
            public void onSaveFaculty(String facultyName, ArrayList<String> departmentList) {
                if (!facultyName.isEmpty() && !departmentList.isEmpty()) {
                    saveFacultyToRealtimeDB(facultyName, departmentList);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Please enter faculty name and department names", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheetDialogFragment.show(getParentFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private void saveFacultyToRealtimeDB(String facultyName, ArrayList<String> departmentList) {
        DatabaseReference newFacultyRef = facultyRef.child(facultyName); // Use faculty name as the key
        String facultyId = newFacultyRef.push().getKey();

        Map<String, Object> facultyData = new HashMap<>();
        facultyData.put("name", facultyName);
        facultyData.put("fid", facultyId);
        newFacultyRef.setValue(facultyData);

        if (!departmentList.isEmpty()) {
            DatabaseReference deptRef = newFacultyRef.child("dept");
            for (String department : departmentList) {
                DatabaseReference newDeptRef = deptRef.push();
                String deptId = newDeptRef.getKey();

                Map<String, Object> deptData = new HashMap<>();
                deptData.put("name", department);
                deptData.put("did", deptId);
                newDeptRef.setValue(deptData);
            }
            Toast.makeText(getContext(), "Faculty added successfully", Toast.LENGTH_SHORT).show();

        }
    }

    private void loadFacultyData() {
        handler = new Handler();
        loadingPB.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        facultyModelArrayList.clear();

        startTimeoutRunnable();

        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingPB.setVisibility(View.GONE);
                Log.d("FirebaseData", "Snapshot Key: " + snapshot.getKey());
                Log.d("FirebaseData", "Snapshot Value: " + snapshot.getValue());

                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String facultyId = facultySnapshot.getKey();
                    String facultyName = facultySnapshot.child("name").getValue(String.class);
                    List<String> departmentNames = new ArrayList<>();

                    for (DataSnapshot deptSnapshot : facultySnapshot.child("dept").getChildren()) {
                        String departmentName = deptSnapshot.child("name").getValue(String.class);
                        departmentNames.add(departmentName);
                    }

                    FacultyModel faculty = new FacultyModel(facultyId, facultyName, departmentNames);
                    facultyModelArrayList.add(faculty);
                }

                adapter.notifyDataSetChanged();
                hasData = true;
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


    private void startTimeoutRunnable() {
        // Initialize the timeout runnable
        timeoutRunnable = () -> {
            // Hide the loading progress bar
            loadingPB.setVisibility(View.GONE);

            if (facultyModelArrayList.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText("No faculty Found");
            }
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
        facultyModelArrayList.clear();
        adapter.notifyDataSetChanged();

        // Call the method to fetch the updated data
        loadFacultyData();
    }

    private boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }


}
