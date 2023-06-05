package com.example.attendance.FacultyModule;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
    ArrayList<FacultyModel> facultyList;
    DatabaseReference facultyRef;
    FirebaseDatabase firebaseDatabase;

    private TextView emptyTextView;
    private Runnable timeoutRunnable;

    private ArrayList<FacultyModel> facultyModelArrayList;
    private ChildEventListener childEventListener;

    EditText editText;

    public FacultyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faculty, container, false);
        recyclerView = view.findViewById(R.id.idRVFacultyt);
        dialog = new Dialog(getContext());
        facultyList = new ArrayList<>();
        adapter = new FacultyAdapter(getContext(), facultyList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        Context context = requireContext();
        facultyModelArrayList = new ArrayList<>();
        loadingPB = view.findViewById(R.id.loading_pb);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        adapter = new FacultyAdapter(context, facultyList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        facultyRef = firebaseDatabase.getReference("Faculty");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.facultyFABtn);
        loadFacultyData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacultyDialog();
            }
        });

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

        return view;
    }

    private void showFacultyDialog() {
        FacultyBottomSheetDialogFragment bottomSheetDialogFragment = new FacultyBottomSheetDialogFragment();
        bottomSheetDialogFragment.setSaveFacultyListener(new FacultyBottomSheetDialogFragment.SaveFacultyListener() {
            @Override
            public void onSaveFaculty(String facultyName, ArrayList<String> departmentList) {
                if (!facultyName.isEmpty() && !departmentList.isEmpty()) {
                    Toast.makeText(getContext(),"worked", Toast.LENGTH_SHORT).show();
                    saveFacultyToRealtimeDB(facultyName, departmentList);
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
        }
    }

    private void loadFacultyData() {
            handler = new Handler();
            //on below line clearing our list.
            loadingPB.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            facultyModelArrayList.clear();
            startTimeoutRunnable();
            //on below line we are calling add child event listener method to read the data.
        facultyRef.addChildEventListener(childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //on below line we are hiding our progress bar.
                    loadingPB.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.GONE);
                    //adding snapshot to our array list on below line.
                    //facultyModelArrayList.add(snapshot.getValue(FacultyModel.class));


                    // Get the data as a HashMap
                    HashMap<String, Object> facultyData = (HashMap<String, Object>) snapshot.getValue();

                    // Extract the relevant information
                    String facultyId = (String) facultyData.get("fid");
                    String facultyName = (String) facultyData.get("name");
                    List<String> departmentNames = (List<String>) facultyData.get("departments");

                    // Create the FacultyModel object
                    FacultyModel faculty = new FacultyModel(facultyId, facultyName, departmentNames);

                    // Add faculty to your ArrayList or adapter as needed
                    facultyModelArrayList.add(faculty);

                    //notifying our adapter that data has changed.
                    adapter.notifyDataSetChanged();

                    hasData = true;
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //this method is called when new child is added we are notifying our adapter and making progress bar visibility as gone.
                    loadingPB.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    //notifying our adapter when child is removed.
                    adapter.notifyDataSetChanged();
                    loadingPB.setVisibility(View.GONE);

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    //notifying our adapter when child is moved.
                    adapter.notifyDataSetChanged();
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


}
