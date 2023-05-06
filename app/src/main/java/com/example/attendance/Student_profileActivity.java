package com.example.attendance;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class Student_profileActivity extends AppCompatActivity {

    private Button studentBtn;
    int SELECT_PICTURE = 200;
    private EditText studentName, studentfaculty, studentdepartment,  studenttnumber;
    ImageView studentImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private StorageReference mStorageref;
    private String courseID;
    Uri selectedImageUri, imageuri;
    ImageView productImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);



        studentBtn = findViewById(R.id.stdntbtn);

        studentName = findViewById(R.id.stdntname);
        studentfaculty = findViewById(R.id.stdntfaculty);
        studentdepartment = findViewById(R.id.stdntdepartment);
        studenttnumber = findViewById(R.id.stdntNumber);

        studentImage = findViewById(R.id.stdntimage);

        //productImageView = findViewById(R.id.product_image);
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        databaseReference = firebaseDatabase.getReference("student");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");

        studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    // pass the constant to compare it
                    // with the returned requestCode
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
                }


            }
        });


        FirebaseUser user = mAuth.getCurrentUser();


        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                loadingPB.setVisibility(View.VISIBLE);



                // on below line we are calling a add value event
                // to pass data to firebase database.
                final String timestamp = String.valueOf(System.currentTimeMillis());
                String filepathname = "student/" + "student" + timestamp;
                Bitmap bitmap = ((BitmapDrawable) studentImage.getDrawable()).getBitmap();
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



                            String stdName = studentName.getText().toString();
                            String stdDepartment = studentdepartment.getText().toString();
                            String stdFaculty = studentfaculty.getText().toString();
                            String stdNum = studenttnumber.getText().toString();

                            Uri productImage = imageuri;
                            String Uid = mAuth.getUid();
                            String productUri = productImage.toString();
                            Student_profileActivity.this.getContentResolver().takePersistableUriPermission(imageuri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                            //String courseImg = productImgBtn.getText().toString();


                            courseID = stdName;
                            // on below line we are passing all data to our modal class.
                            StudentModal studentModal = new StudentModal(courseID, stdName, stdDepartment, stdFaculty, downloadUri, Uid);

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // on below line we are setting data in our firebase database.
                                    databaseReference.child(courseID).setValue(studentModal);
                                    // displaying a toast message.
                                    Toast.makeText(Student_profileActivity.this, "Student Added..", Toast.LENGTH_SHORT).show();
                                    // starting a main activity.
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.popBackStack();
                                    finish();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // displaying a failure message on below line.
                                    Toast.makeText(Student_profileActivity.this, "Failed to add Student..", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onCancelled: ",error.toException() );


                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingPB.setVisibility(View.GONE);
                        Toast.makeText(Student_profileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    }
                });


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
                    Picasso.get().load(imageuri).into(studentImage);


                }
            }
        }
    }

}
