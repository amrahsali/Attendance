package com.example.attendance;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created By: George David
 * Date: May 2023
 */
public class AddStaffFragment extends Fragment {

    private FirebaseAuth mAuth;
    Button create_staff;
    EditText username, phoneNumber, emailad, department1, faculty1;
    ImageView profileimg;
    private ProgressBar loadingPB;
    int SELECT_PICTURE = 200;
    Uri selectedImageUri, imageuri;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private StorageReference mStorageref;
    private String courseID;
    TextView name,email;
    private static final int ADD_COURSE_REQUEST = 1;
    private RecyclerView coursesRV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_staff, container, false);
        create_staff = view.findViewById(R.id.loginbtn);
        username = view.findViewById(R.id.username);
        faculty1 = view.findViewById(R.id.faculty);
        department1 = view.findViewById(R.id.department);
        phoneNumber = view.findViewById(R.id.mobile_np);
        profileimg = view.findViewById(R.id.userprofile);
        emailad = view.findViewById(R.id.email);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Staff");
        mStorageref = FirebaseStorage.getInstance().getReference("Upload Photos");


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
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }

        });
        //get user profile
        FirebaseUser user = mAuth.getCurrentUser();

        create_staff.setOnClickListener(v -> {


            // loadingPB.setVisibility(View.VISIBLE);
            // on below line we are calling a add value event
            // to pass data to firebase database.
            final String timestamp = String.valueOf(System.currentTimeMillis());
            String filepathname = "Staff/" + "staff" + timestamp;
            Bitmap bitmap = ((BitmapDrawable) profileimg.getDrawable()).getBitmap();
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
                        getActivity().getContentResolver().takePersistableUriPermission(imageuri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                        //String courseImg = productImgBtn.getText().toString();
                        courseID = name;
                        // on below line we are passing all data to our modal class.
                        StaffRVModal courseRVModal = new StaffRVModal(courseID, name, email, phone, downloadUri, Uid, faculty, department );

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // on below line we are setting data in our firebase database.
                                databaseReference.child(courseID).setValue(courseRVModal);
                                // displaying a toast message.
                                Toast.makeText(getContext(), "Product Added..", Toast.LENGTH_SHORT).show();
                                // starting a main activity.
                                startActivity(new Intent(getContext(), StaffListFragment.class));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // displaying a failure message on below line.
                                Toast.makeText(getContext(), "Failed to add Product..", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onCancelled: ",error.toException() );
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            });


        });
        return view;
    }

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