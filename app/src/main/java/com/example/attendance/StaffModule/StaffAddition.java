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

    AutoCompleteTextView faculty1, department1;
    List<String> facultyList;
    List<String> departmentList;
    DatabaseReference facultyRef;

    private Map<String, List<String>> facultyDepartmentsMap;
    private ArrayAdapter<String> facultyAdapter;
    private ArrayAdapter<String> departmentAdapter;
    private AutoCompleteTextView facultyAutoComplete;
    private AutoCompleteTextView departmentAutoComplete;


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
        facultyDepartmentsMap = new HashMap<>();
        facultyDepartmentsMap = new HashMap<>();
        facultyAutoComplete = findViewById(R.id.faculty);
        departmentAutoComplete = findViewById(R.id.department);
        facultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        facultyAutoComplete.setAdapter(facultyAdapter);
        departmentAutoComplete.setAdapter(departmentAdapter);

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
        facultyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFaculty = (String) parent.getItemAtPosition(position);
            updateDepartmentDropdown(selectedFaculty);
        });


        create_staff.setOnClickListener(v -> {

        if (!username.getText().toString().isEmpty() && !emailad.getText().toString().isEmpty()
                && !phoneNumber.getText().toString().isEmpty() && !department1.getText().toString().isEmpty() && imageuri != null
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
                final String timestamp = String.valueOf(System.currentTimeMillis());
                String filepathname = "Staff/" + "staff" + timestamp;
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
                            String email = emailad.getText().toString();
                            String phone = phoneNumber.getText().toString();
                            String department = department1.getText().toString();
                            String faculty = faculty1.getText().toString();

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
            if (department1.getText().toString().isEmpty()){
                department1.setError("fill");
            }
            if (faculty1.getText().toString().isEmpty()){
                faculty1.setError("fill");
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
                    //IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
    private void loadFacultyData() {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                    String facultyId = facultySnapshot.getKey();
                    String facultyName = facultySnapshot.child("name").getValue(String.class);
                    List<String> departmentNames = new ArrayList<>();

                    for (DataSnapshot deptSnapshot : facultySnapshot.child("dept").getChildren()) {
                        String departmentName = deptSnapshot.child("name").getValue(String.class);
                        departmentNames.add(departmentName);
                    }

                    facultyDepartmentsMap.put(facultyName, departmentNames);
                }

                facultyAdapter.addAll(facultyDepartmentsMap.keySet());
                facultyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e(TAG, "Failed to load faculty data: " + databaseError.getMessage());
            }
        });
    }
    private void updateDepartmentDropdown(String selectedFaculty) {
        List<String> departmentNames = facultyDepartmentsMap.get(selectedFaculty);

        if (departmentNames != null) {
            departmentAdapter.clear();
            departmentAdapter.addAll(departmentNames);
            departmentAdapter.notifyDataSetChanged();
        }
    }
}