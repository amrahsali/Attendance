package com.example.attendance.Utility;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.attendance.LoginModule.Login;
import com.example.attendance.LoginModule.MainActivity;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final int MAX_RETRIES = 3;
    ImageView print;
    TextView statusText;
    int click = 1;
    Fingerprint fingerprint;
    private ScanUtils scanUtils;
    ProgressBar progressBar;

    Button retryButton;

    private int retryCount = 0;


    public interface ImageLoadCallback {
        void onImageLoaded(byte[] imageData);
        void onImageLoadFailed(Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        print = findViewById(R.id.print_image);
        progressBar = findViewById(R.id.idPBLoading);
        statusText = findViewById(R.id.status);
        retryButton = findViewById(R.id.retry_button);
        fingerprint = new Fingerprint();
        scanUtils = new ScanUtils(ScanActivity.this, print, print, statusText);

        // Set a callback for the fingerprint scan result
        scanUtils.setScanCallback(new ScanUtils.ScanCallback() {
            @Override
            public void onFingerprintScanned(byte[] scannedFingerprint) {
                // Fingerprint scan result received, call compareFingerprint method
                compareFingerprint(scannedFingerprint);
                progressBar.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                //Toast.makeText(ScanActivity.this, "got results", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanError(String errorMessage) {
                // Handle scan errors if needed
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScanActivity.this, "Scan Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                retryButton.setVisibility(View.VISIBLE);
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryCount = 0;
                progressBar.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                scanUtils.scan(ScanActivity.this);
            }
        });



    }

    @Override
    protected void onStop() {
        progressBar.setVisibility(View.GONE);
        scanUtils.stopScan();
        super.onStop();
    }

    @Override
    protected void onStart() {
        scanUtils.scan(ScanActivity.this);
        super.onStart();
    }

    // Method to perform fingerprint matching
    private boolean matchFingerprints(byte[] probe, byte[] candidate) throws IOException {
        boolean matches = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            FingerprintTemplate probe1 = new FingerprintTemplate(
                    new FingerprintImage(probe));

            FingerprintTemplate candidate1 = new FingerprintTemplate(
                    new FingerprintImage(candidate));

            FingerprintMatcher matcher = new FingerprintMatcher(candidate1);
            double similarity = matcher.match(probe1);

            double threshold = 40;
            matches = similarity >= threshold;
        }

        return matches;
    }

    private void loadImageFromUrl(String imageUrl, ImageLoadCallback callback) {
        Glide.with(ScanActivity.this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        // Once the image is loaded, you can convert it to a byte array
                        byte[] imageData = bitmapToByteArray(bitmap);

                        // Now you can use the imageData by invoking the callback
                        callback.onImageLoaded(imageData);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Do nothing
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        // Handle the case where image loading failed
                        callback.onImageLoadFailed(new Exception("Failed to load image from URL"));
                    }
                });
    }


    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private void compareFingerprint(byte[] scannedFingerprint) {

        List<StaffRVModal> staffList = LocalStorageUtil.retrieveStaffDataFromLocalStorage(this);
        if (staffList != null){
            for (StaffRVModal staff : staffList) {
                if (staff.getRightFinger() != null || staff.getLeftFinger() != null) {
                    ImageLoadCallback callback = new ImageLoadCallback() {
                        @Override
                        public void onImageLoaded(byte[] imageData) {
                            // Perform fingerprint matching using the loaded imageData
                            try {
                                boolean leftMatch = matchFingerprints(scannedFingerprint, imageData);
                                boolean rightMatch = matchFingerprints(scannedFingerprint, imageData);

                                if (leftMatch || rightMatch) {
//                                    String customToken = LocalStorageUtil.generateCustomToken(staff.getUserID()); // Use staff UID or any unique identifier
//                                    signInWithCustomToken(customToken);
                                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                                    intent.putExtra("staff_name", staff.getProductName());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ScanActivity.this, "matching fingerprint....", Toast.LENGTH_SHORT).show();
                                    retryButton.setVisibility(View.VISIBLE);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onImageLoadFailed(Exception e) {
                            // Handle the case where image loading failed
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ScanActivity.this, "Failed to load image from URL.", Toast.LENGTH_SHORT).show();
                            retryButton.setVisibility(View.VISIBLE);
                        }
                    };

                    if (staff.getRightFinger() != null) {
                        loadImageFromUrl(staff.getRightFinger(), callback);
                    } else {
                        loadImageFromUrl(staff.getLeftFinger(), callback);
                    }
                }
            }
        }

        progressBar.setVisibility(View.GONE);
//        Toast.makeText(ScanActivity.this, "Fingerprint not recognized.", Toast.LENGTH_SHORT).show();

//        FirebaseDatabase.getInstance().getReference().child("Staff")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot staffSnapshot : snapshot.getChildren()) {
//                            StaffRVModal staff = staffSnapshot.getValue(StaffRVModal.class);
//                            Log.i(TAG, "onDataChange: " + staff.getRightFinger());
//
//                            if (staff.getRightFinger() != null || staff.getLeftFinger() != null) {
//                                ImageLoadCallback callback = new ImageLoadCallback() {
//                                    @Override
//                                    public void onImageLoaded(byte[] imageData) {
//                                        // Perform fingerprint matching using the loaded imageData
//                                        try {
//                                            boolean leftMatch = matchFingerprints(scannedFingerprint, imageData);
//                                            boolean rightMatch = matchFingerprints(scannedFingerprint, imageData);
//
//                                            if (leftMatch || rightMatch) {
//                                                progressBar.setVisibility(View.GONE);
//                                                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
//                                                intent.putExtra("staff_name", staff.getProductName());
//                                                startActivity(intent);
//                                            } else {
//                                                // No matching fingerprint found, perform your action here
//                                                // For example, show an error message
//                                                progressBar.setVisibility(View.GONE);
//                                                Toast.makeText(ScanActivity.this, "Fingerprint not recognized.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        } catch (IOException e) {
//                                            throw new RuntimeException(e);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onImageLoadFailed(Exception e) {
//                                        // Handle the case where image loading failed
//                                        progressBar.setVisibility(View.GONE);
//                                        Toast.makeText(ScanActivity.this, "Failed to load image from URL.", Toast.LENGTH_SHORT).show();
//                                    }
//                                };
//
//                                if (staff.getRightFinger() != null) {
//                                    loadImageFromUrl(staff.getRightFinger(), callback);
//                                } else {
//                                    loadImageFromUrl(staff.getLeftFinger(), callback);
//                                }
//                            }
//                        }
//
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(ScanActivity.this, "Fingerprint not recognized.", Toast.LENGTH_SHORT).show();
//
////
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Error occurred while fetching staff data
//                        Toast.makeText(ScanActivity.this, "Failed to retrieve staff data.", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }


    private void handleFingerprintMismatch() {
        retryCount++;
        if (retryCount < MAX_RETRIES) {
            Toast.makeText(ScanActivity.this, "Matching failed, retrying... (" + retryCount + ")", Toast.LENGTH_SHORT).show();
            scanUtils.scan(ScanActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            Toast.makeText(ScanActivity.this, "Fingerprint not recognized after multiple attempts.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithCustomToken(String customToken) {
        FirebaseAuth.getInstance().signInWithCustomToken(customToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Toast.makeText(ScanActivity.this, "fire success: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                            intent.putExtra("staff_name", user.getDisplayName()); // Pass the staff name or email
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(ScanActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
