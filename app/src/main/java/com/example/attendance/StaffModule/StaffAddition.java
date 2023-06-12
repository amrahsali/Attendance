package com.example.attendance.StaffModule;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.FacultyModule.FacultyModel;
import com.example.attendance.R;
import com.example.attendance.Utility.CustomSpinnerAdapter;
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

public class StaffAddition extends AppCompatActivity {

    private static final int CAMERA_IMAGE_REQUEST_CODE = -1;
    private FirebaseAuth mAuth;
    Button create_staff, Cancel, Save;
    Dialog staffBiometricDialog;;
    EditText username, phoneNumber, emailad;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;
    DatabaseReference facultyRef;
    private Spinner facultySpinner;
    private Spinner departmentSpinner;

    private Map<String, List<String>> facultyDepartmentsMap;
    private ArrayAdapter<String> facultyAdapter;
    private ArrayAdapter<String> departmentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_staff);
        mAuth = FirebaseAuth.getInstance();
        create_staff = findViewById(R.id.loginbtn);
        username = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.mobile_np);
        profileimg = findViewById(R.id.userprofile);
        emailad = findViewById(R.id.email);
        firebaseDatabase = FirebaseDatabase.getInstance();
        facultyRef = firebaseDatabase.getReference("Faculty");
        databaseReference = firebaseDatabase.getReference("Staff");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        staffBiometricDialog = new Dialog(this);
        // Initialize the spinners
        facultySpinner = findViewById(R.id.faculty_spinner);
        departmentSpinner = findViewById(R.id.department_spinner);

        // Initialize the adapters
        facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapters to the spinners
        facultySpinner.setAdapter(facultyAdapter);
        departmentSpinner.setAdapter(departmentAdapter);

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

//        profileimg.setOnClickListener(v -> {
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, SELECT_PICTURE);
//        });

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



        create_staff.setOnClickListener(v -> {

        if (!username.getText().toString().isEmpty() && !emailad.getText().toString().isEmpty()
                && !phoneNumber.getText().toString().isEmpty() && imageuri != null
                ) {

            staffBiometricDialog.setContentView(R.layout.biometric_dialog);
            staffBiometricDialog.create();
            staffBiometricDialog.show();
            Save = staffBiometricDialog.findViewById(R.id.add_print_save);
            Cancel = staffBiometricDialog.findViewById(R.id.add_print_cancel);
            Save.setOnClickListener(view2 -> {
                // loadingPB.setVisibility(View.VISIBLE);
                // on below line we are calling a add value event
                // to pass data to firebase database.
                Save.setEnabled(false);
                final String timestamp = String.valueOf(System.currentTimeMillis());
                String filepathname = "Staff/" + "staff" + timestamp;
                Drawable drawable = profileimg.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(filepathname);
                storageReference1.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                    // getting the url of image uploaded
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String downloadUri = uriTask.getResult().toString();
                    if (uriTask.isSuccessful()) {
                        String name = username.getText().toString();
                        String email = emailad.getText().toString();
                        String phone = phoneNumber.getText().toString();
                        String department = departmentSpinner.getSelectedItem().toString();
                        String faculty = facultySpinner.getSelectedItem().toString();

                        Uri staffImage = imageuri;
                        String Uid = mAuth.getUid();
                        String staffImageUri = staffImage.toString();
                        StaffAddition.this.getContentResolver().takePersistableUriPermission(imageuri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                        courseID = name;
                        // on below line we are passing all data to our modal class.


                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // on below line we are setting data in our firebase database.

                                StaffRVModal courseRVModal = new StaffRVModal(courseID, name, email, phone, downloadUri, Uid, faculty, department);

                                databaseReference.child(courseID).setValue(courseRVModal);
                                // displaying a toast message.
                                Toast.makeText(StaffAddition.this, "Staff Created..", Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.popBackStack();
                                staffBiometricDialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // displaying a failure message on below line.
                                Toast.makeText(StaffAddition.this, "Failed to add Staff..", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCancelled: ", error.toException());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(StaffAddition.this, "Failed: Server Error. Contact Admin", Toast.LENGTH_LONG).show();
                    }
                });
            });
            Cancel.setOnClickListener(view2 -> staffBiometricDialog.dismiss());
        }else {
            Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            if (username.getText().toString().isEmpty()){
                username.setError("fill");
            }
            if (emailad.getText().toString().isEmpty()){
                emailad.setError("fill");
            }
            if (phoneNumber.getText().toString().isEmpty()){
                phoneNumber.setError("fill");
            }
        }
        });
    }

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
                }
            }

        }
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//          // Match the request 'pic id with requestCode
//        if (requestCode == SELECT_PICTURE) {
//            // BitMap is data structure of image file which store the image in memory
//            selectedImageUri = data.getData();
//            Toast.makeText(StaffAddition.this, "code is: "+ selectedImageUri, Toast.LENGTH_SHORT).show();
//            if (selectedImageUri != null) {
//                imageuri = selectedImageUri;
//                Picasso.get().load(imageuri).into(profileimg);
//            }
//        }
//    }
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

        if (faculty != null) {
            List<String> departmentNames = facultyDepartmentsMap.get(faculty);
            if (departmentNames != null) {
                departmentAdapter.addAll(departmentNames);
            }
        }

        departmentAdapter.notifyDataSetChanged();
    }
}