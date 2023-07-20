package com.example.attendance.Utility;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.attendance.LoginModule.MainActivity;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import asia.kanopi.uareu4500library.Status;

public class ScanActivity extends AppCompatActivity {
    ImageView print,  testImage1, testImaqge2;
    TextView statusText;
    int click = 1;
    Fingerprint fingerprint;
    private ScanUtils scanUtils;
    ProgressBar progressBar;

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
//        testImage1 = findViewById(R.id.test1);
//        testImaqge2 = findViewById(R.id.test2);
        statusText = findViewById(R.id.status);
        fingerprint = new Fingerprint();
        scanUtils = new ScanUtils(ScanActivity.this, print, print, statusText);

        // Set a callback for the fingerprint scan result
        scanUtils.setScanCallback(new ScanUtils.ScanCallback() {
            @Override
            public void onFingerprintScanned(byte[] scannedFingerprint) {
                // Fingerprint scan result received, call compareFingerprint method
                compareFingerprint(scannedFingerprint);
                progressBar.setVisibility(View.VISIBLE);
                //Toast.makeText(ScanActivity.this, "got results", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScanError(String errorMessage) {
                // Handle scan errors if needed
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ScanActivity.this, "Scan Error: " + errorMessage, Toast.LENGTH_SHORT).show();
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
//        Bitmap rightBitmap = BitmapFactory.decodeByteArray(probe, 0, probe.length);
//        Bitmap leftBitmap = BitmapFactory.decodeByteArray(candidate, 0, candidate.length);
//        if (rightBitmap != null) {
//            byte[] rightBmpData = AndroidBmpUtil.convertToBmp24bit(probe);
//            if (rightBmpData != null) {
//                // Save the rightBmpData to the database
//                testImaqge2.setImageBitmap(rightBitmap);
//
//            }
//        }
//        if (leftBitmap != null) {
//            byte[] leftBmpData = AndroidBmpUtil.convertToBmp24bit(candidate);
//            if (leftBmpData != null) {
//                // Save the rightBmpData to the database
//                testImage1.setImageBitmap(leftBitmap);
//
//            }
//        }
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
        FirebaseDatabase.getInstance().getReference().child("Staff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot staffSnapshot : snapshot.getChildren()) {
                            StaffRVModal staff = staffSnapshot.getValue(StaffRVModal.class);
                            Log.i(TAG, "onDataChange: " + staff.getRightFinger());

                            if (staff.getRightFinger() != null || staff.getLeftFinger() != null) {
                                ImageLoadCallback callback = new ImageLoadCallback() {
                                    @Override
                                    public void onImageLoaded(byte[] imageData) {
                                        // Perform fingerprint matching using the loaded imageData
                                        try {
                                            boolean leftMatch = matchFingerprints(scannedFingerprint, imageData);
                                            boolean rightMatch = matchFingerprints(scannedFingerprint, imageData);

                                            if (leftMatch || rightMatch) {
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                                                intent.putExtra("staff_name", staff.getProductName());
                                                startActivity(intent);
                                            } else {
                                                // No matching fingerprint found, perform your action here
                                                // For example, show an error message
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ScanActivity.this, "Fingerprint not recognized.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    @Override
                                    public void onImageLoadFailed(Exception e) {
                                        // Handle the case where image loading failed
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ScanActivity.this, "Failed to load image from URL.", Toast.LENGTH_SHORT).show();
                                    }
                                };

                                if (staff.getRightFinger() != null) {
                                    loadImageFromUrl(staff.getRightFinger(), callback);
                                } else {
                                    loadImageFromUrl(staff.getLeftFinger(), callback);
                                }
                            }
                        }

//                        // No matching fingerprint found, perform your action here
//                        // For example, show an error message
//                        Toast.makeText(ScanActivity.this, "Fingerprint not recognized end.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error occurred while fetching staff data
                        Toast.makeText(ScanActivity.this, "Failed to retrieve staff data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
