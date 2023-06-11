package com.example.attendance.StudentModule;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAddition extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button create_student, Cancel, Save,  SaveCourse, CancelCourse;
    ImageButton selectCourse;
    Dialog studentBiometricDialog, courseDialog;
    EditText username, matricNumber, level;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;

    private LinearLayout coursesListLayout;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;

    private Spinner facultySpinner, departmentSpinner, coursesSpinner;

    private Map<String, List<String>> facultyDepartmentsMap;
    private ArrayAdapter<String> facultyAdapter;

    private ArrayAdapter<String> coursesAdapter;
    private ArrayAdapter<String> departmentAdapter;

    private ArrayList<String> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_addition);
        initUI();

        profileimg.setOnClickListener(v -> {
            // create an instance of the
            // intent of the type image

            if (Build.VERSION.SDK_INT <19){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            } else {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }

        });

        loadFacultyData();

        facultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFaculty = (String) parent.getItemAtPosition(position);
                updateDepartmentDropdown(selectedFaculty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        //get user profile
        create_student.setOnClickListener(v -> {

            if (!username.getText().toString().isEmpty() && !level.getText().toString().isEmpty()
                    && !matricNumber.getText().toString().isEmpty() && imageuri != null
            ) {
                studentBiometricDialog.setContentView(R.layout.biometric_dialog);
                courseDialog.setContentView(R.layout.student_course_dialog);
                SaveCourse = courseDialog.findViewById(R.id.add_stn_course_save);
                CancelCourse = courseDialog.findViewById(R.id.add_stn_course_cancel);
                Save = studentBiometricDialog.findViewById(R.id.add_print_save);
                Cancel = studentBiometricDialog.findViewById(R.id.add_print_cancel);
                coursesSpinner = courseDialog.findViewById(R.id.course_spinner);
                selectCourse = courseDialog.findViewById(R.id.select_courses_btn);
                coursesListLayout = courseDialog.findViewById(R.id.courses_list_layout);
                //loadingPB = courseDialog.findViewById(R.id.idPBLoading);


                courseDialog.create();
                courseDialog.show();
                loadCoursesData();
                coursesSpinner.setAdapter(coursesAdapter);

                selectCourse.setOnClickListener(v1 -> {
                    String courseName = coursesSpinner.getSelectedItem().toString();
                    if (!courseName.equals("select course") && !coursesList.contains(courseName)) {
                        coursesList.add(courseName);
                        addDepartmentToLayout(courseName);
                    }
                });
                SaveCourse.setOnClickListener(view ->{
                    if (!coursesList.isEmpty()){
                        studentBiometricDialog.create();
                        studentBiometricDialog.show();
                        courseDialog.dismiss();
                        //loadingPB.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(this,"Please add course",Toast.LENGTH_SHORT);
                    }

                        });
                CancelCourse.setOnClickListener(c->courseDialog.dismiss());

                Save.setOnClickListener(view2 -> {
                    // loadingPB.setVisibility(View.VISIBLE);
                    // on below line we are calling a add value event
                    // to pass data to firebase database.
                   // loadingPB.setVisibility(View.VISIBLE);
                    final String timestamp = String.valueOf(System.currentTimeMillis());
                    String filepathname = "Student/" + "student" + timestamp;
                    Drawable drawable = profileimg.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] data = byteArrayOutputStream.toByteArray();

                    StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(filepathname);
                    storageReference1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // getting the url of image uploaded
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            String downloadUri = uriTask.getResult().toString();
                            if (uriTask.isSuccessful()) {
                                String name = username.getText().toString();
                                String matric = matricNumber.getText().toString();
                                String department = departmentSpinner.getSelectedItem().toString();
                                String faculty = facultySpinner.getSelectedItem().toString();
                                String level1 =  level.getText().toString();

                                Uri staffImage = imageuri;
                                String Uid = mAuth.getUid();
                                String staffImageUri = staffImage.toString();
                                StudentAddition.this.getContentResolver().takePersistableUriPermission(imageuri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                                courseID = name;
                                // on below line we are passing all data to our modal class.


                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // on below line we are setting data in our firebase database.

                                        StudentModal studentModal = new StudentModal(courseID, name, faculty, department, matric, Uid, level1, downloadUri, coursesList);

                                        databaseReference.child(courseID).setValue(studentModal);
                                        // displaying a toast message.
                                        //loadingPB.setVisibility(View.GONE);
                                        Toast.makeText(StudentAddition.this, "Student added..", Toast.LENGTH_SHORT).show();

                                        studentBiometricDialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // displaying a failure message on below line.
                                        Toast.makeText(StudentAddition.this, "Failed to add Staff..", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onCancelled: ", error.toException());
                                        loadingPB.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingPB.setVisibility(View.GONE);
                            Toast.makeText(StudentAddition.this, "Failed: Server Error. Contact Admin", Toast.LENGTH_LONG).show();
                        }
                    });
                });
                Cancel.setOnClickListener(view2 -> studentBiometricDialog.dismiss());
            }else {

                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                if (username.getText().toString().isEmpty()){
                    username.setError("fill");
                }
                if (level.getText().toString().isEmpty()){
                    level.setError("fill");
                }
                if (matricNumber.getText().toString().isEmpty()){
                    matricNumber.setError("fill");
                }
                if (imageuri == null) {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout

                    imageuri = data.getData();
                    Picasso.get().load(imageuri).into(profileimg);
                    //IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private void loadFacultyData() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("Faculty");
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                facultyDepartmentsMap = new HashMap<>(); // Initialize the map
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                    String facultyName = facultySnapshot.child("name").getValue(String.class);
                    List<String> departmentNames = new ArrayList<>();
                    for (DataSnapshot deptSnapshot : facultySnapshot.child("dept").getChildren()) {
                        String departmentName = deptSnapshot.child("name").getValue(String.class);
                        if (departmentName != null) {
                            departmentNames.add(departmentName);
                        }
                    }
                    if (facultyName != null && !departmentNames.isEmpty()) {
                        facultyDepartmentsMap.put(facultyName, departmentNames);
                    }
                }
                facultyAdapter.addAll(facultyDepartmentsMap.keySet());
                facultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load faculty data: " + databaseError.getMessage());
            }
        });
    }

    private void updateDepartmentDropdown(String faculty) {
        departmentAdapter.clear();

        if (!faculty.equals("Select Faculty")) {
            List<String> departmentNames = facultyDepartmentsMap.get(faculty);
            if (departmentNames != null) {
                departmentAdapter.addAll(departmentNames);
            }
        }

        departmentAdapter.notifyDataSetChanged();
    }

    private void loadCoursesData(){
        coursesAdapter.add("Select course");
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> courseNames = new ArrayList<>();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseName = courseSnapshot.child("courseName").getValue(String.class);
                    courseNames.add(courseName);
                }
                coursesAdapter.addAll(courseNames);
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load courses data: " + databaseError.getMessage());
            }
        });
    }

    private void initUI(){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        create_student = findViewById(R.id.create_student);
        username = findViewById(R.id.stdntname);
        matricNumber = findViewById(R.id.stdntNumber);
        profileimg = findViewById(R.id.stdntimage);
        level = findViewById(R.id.student_level);

        facultySpinner = findViewById(R.id.fac_std_spinner);
        departmentSpinner = findViewById(R.id.dep_std_spinner);


        studentBiometricDialog = new Dialog(this);
        courseDialog = new Dialog(this);
        facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        coursesAdapter = new ArrayAdapter<>(StudentAddition.this, android.R.layout.simple_spinner_item);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesList = new ArrayList<>();
        // Set the adapters to the spinners
        facultySpinner.setAdapter(facultyAdapter);
        departmentSpinner.setAdapter(departmentAdapter);
    }

    private void addDepartmentToLayout(String departmentName) {
        TextView coursesTextView = new TextView(this);
        coursesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        coursesTextView.setText(departmentName);
        coursesTextView.setTextColor(Color.BLACK);
        coursesTextView.setTextSize(16);
        coursesListLayout.addView(coursesTextView);
    }
}