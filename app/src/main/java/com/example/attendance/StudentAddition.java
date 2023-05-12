package com.example.attendance;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class StudentAddition extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button create_student, Cancel, Save;
    Dialog studentBiometricDialog;;
    EditText username, matricNumber, level, department1, faculty1;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_addition);
        mAuth = FirebaseAuth.getInstance();
        create_student = findViewById(R.id.create_student);
        username = findViewById(R.id.stdntname);
        faculty1 = findViewById(R.id.stdntfaculty);
        department1 = findViewById(R.id.stdntdepartment);
        matricNumber = findViewById(R.id.stdntNumber);
        profileimg = findViewById(R.id.stdntimage);
        level = findViewById(R.id.student_level);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");
        studentBiometricDialog = new Dialog(this);

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
        //get user profile
        FirebaseUser user = mAuth.getCurrentUser();

        create_student.setOnClickListener(v -> {

            if (!username.getText().toString().isEmpty() && !level.getText().toString().isEmpty()
                    && !matricNumber.getText().toString().isEmpty() && !department1.getText().toString().isEmpty()
            ) {
                studentBiometricDialog.setContentView(R.layout.biometric_dialog);
                studentBiometricDialog.create();
                studentBiometricDialog.show();
                Save = studentBiometricDialog.findViewById(R.id.add_print_save);
                Cancel = studentBiometricDialog.findViewById(R.id.add_print_cancel);
                Save.setOnClickListener(view2 -> {
                    // loadingPB.setVisibility(View.VISIBLE);
                    // on below line we are calling a add value event
                    // to pass data to firebase database.
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
                                String department = department1.getText().toString();
                                String faculty = faculty1.getText().toString();
                                String studentLevel = level.getText().toString();

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

                                        StudentModal studentModal = new StudentModal(courseID, name, faculty, department, matric, Uid, studentLevel, downloadUri);

                                        databaseReference.child(courseID).setValue(studentModal);
                                        // displaying a toast message.
                                        Toast.makeText(StudentAddition.this, "Student added..", Toast.LENGTH_SHORT).show();

                                        studentBiometricDialog.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // displaying a failure message on below line.
                                        Toast.makeText(StudentAddition.this, "Failed to add Staff..", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onCancelled: ", error.toException());
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
                if (department1.getText().toString().isEmpty()){
                    department1.setError("fill");
                }
                if (faculty1.getText().toString().isEmpty()){
                    faculty1.setError("fill");
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
}