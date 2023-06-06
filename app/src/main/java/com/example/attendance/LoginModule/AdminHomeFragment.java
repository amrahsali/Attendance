package com.example.attendance.LoginModule;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.attendance.FacultyModule.FacultyFragment;
import com.example.attendance.StaffModule.StaffListFragment;
import com.example.attendance.R;
import com.example.attendance.StudentModule.Add_studentFragment;
import com.example.attendance.Utility.ImagePagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class AdminHomeFragment extends Fragment {

    Dialog dialog;
    Button button;
    TextView nameTextView, department, faculty, course;
    private ViewPager imageViewPager;

    CardView addStudentCard, addStaffCard, facultyCard, departmentCard;

    public AdminHomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

         addStaffCard = view.findViewById(R.id.card_staff);
         addStudentCard = view.findViewById(R.id.card_students);
         facultyCard = view.findViewById(R.id.card_faculties);
         departmentCard = view.findViewById(R.id.card_departments);

        addStaffCard.setOnClickListener(v -> navigateToStaffFragment());

        addStudentCard.setOnClickListener(v -> navigateToStudentFragment());
        
        facultyCard.setOnClickListener(v -> navigateToFacultyFragment());
                
        departmentCard.setOnClickListener(v -> navigateToDepartmentFragment());

        // Get reference to the ViewPager
        imageViewPager = view.findViewById(R.id.imageViewPager);

        // Create an array of image resource IDs
        int[] imageIds = {R.mipmap.slider1_1hdpi, R.mipmap.slider2_1hdpi, R.mipmap.slider3hdpi};

        // Create an instance of the custom PagerAdapter
        // Create an instance of the custom PagerAdapter
        ImagePagerAdapter adapter = new ImagePagerAdapter(getContext(), imageIds);

        // Set the adapter on the ViewPager
        imageViewPager.setAdapter(adapter);

        // Set up auto sliding
        final int NUM_PAGES = imageIds.length;
        final long DELAY_MS = 3000; // Delay between slides in milliseconds
        final long PERIOD_MS = 3000; // Interval between slide changes in milliseconds

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            public void run() {
                int currentItem = imageViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % NUM_PAGES;
                imageViewPager.setCurrentItem(nextItem);
            }
        };

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, DELAY_MS, PERIOD_MS);
                        

//        button = view.findViewById(R.id.updtProfile_btn);
//
//        nameTextView = view.findViewById(R.id.username);
//        department = view.findViewById(R.id.userdepartment);
//        faculty = view.findViewById(R.id.userfaculty);
//        course = view.findViewById(R.id.usercourse);


//        dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.activity_dialog_box2);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.setCancelable(false);
//
//        EditText nameEditText = dialog.findViewById(R.id.edt_name);
//        EditText facultyEditText = dialog.findViewById(R.id.edt_faculty);
//        EditText departmentEditText = dialog.findViewById(R.id.edt_department);
//        EditText courseEditText = dialog.findViewById(R.id.edt_course);
//
//        Button saveButton = dialog.findViewById(R.id.add_course_save);
//
//        saveButton.setOnClickListener((View v) -> {
//                    String name = nameEditText.getText().toString();
//                    String facty = facultyEditText.getText().toString();
//                    String dptment = departmentEditText.getText().toString();
//                    String courses = courseEditText.getText().toString();
//
//
//            nameTextView.setText(name);
//            department.setText(dptment);
//            faculty.setText(facty);
//            course.setText(courses);
//
//
//
//
//
//            dialog.dismiss();
//        });
//
//
//        button.setOnClickListener((View v) -> {
//            dialog.show();
//        });

        return view;


    }

    private void navigateToDepartmentFragment() {
        Fragment fragment = new FacultyFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private void navigateToFacultyFragment() {
        Fragment fragment = new FacultyFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private void navigateToStudentFragment() {
        Fragment fragment = new Add_studentFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }

    private void navigateToStaffFragment() {
        Fragment fragment = new StaffListFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }
}