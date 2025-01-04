package com.example.attendance.ExamsModule;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.attendance.LoginModule.MainActivity;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.example.attendance.StudentModule.StudentModal;
import com.example.attendance.Utility.Fingerprint;
import com.example.attendance.Utility.LocalStorageUtil;
import com.example.attendance.Utility.ScanActivity;
import com.example.attendance.Utility.ScanUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExamsScanActivity extends AppCompatActivity {

    ImageView print;
    TextView statusText;
    int click = 1;
    Fingerprint fingerprint;
    private ScanUtils scanUtils;
    ProgressBar progressBar;
    String examsName = "";
    String examsTime = "";
    String examsEndTime = "";

    Button examsRetryBtn;

    public interface ImageLoadCallback {
        void onImageLoaded(byte[] imageData);
        void onImageLoadFailed(Exception e);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_scan);

        print = findViewById(R.id.exam_print_image);
        LottieAnimationView animationView = findViewById(R.id.print_animation);
        animationView.playAnimation();
        progressBar = findViewById(R.id.idPBLoading);
        statusText = findViewById(R.id.status);
        examsRetryBtn = findViewById(R.id.exams_retry_btn);
        fingerprint = new Fingerprint();
        Intent intent = getIntent();
        examsName = intent.getStringExtra("ExamsName");
        examsTime = intent.getStringExtra("time");
        examsEndTime = intent.getStringExtra("examsEndTime");
        scanUtils = new ScanUtils(ExamsScanActivity.this, print, print, statusText);

        examsRetryBtn.setOnClickListener(x->{
            scanUtils.scan(ExamsScanActivity.this);
        });

        // Set a callback for the fingerprint scan result
        scanUtils.setScanCallback(new ScanUtils.ScanCallback() {
            @Override
            public void onFingerprintScanned(byte[] scannedFingerprint) {
                // Fingerprint scan result received, call compareFingerprint method
                compareFingerprint(scannedFingerprint);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScanError(String errorMessage) {
                // Handle scan errors if needed
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExamsScanActivity.this, "Scan Error: " + errorMessage, Toast.LENGTH_SHORT).show();
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
        scanUtils.scan(ExamsScanActivity.this);
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
        Glide.with(ExamsScanActivity.this)
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

        List<StudentModal> students = LocalStorageUtil.retrieveStudentDataFromLocalStorage(this);


        for (StudentModal student : students) {
            Log.i(TAG, "Comparing fingerprint for student: " + student.getStudentName());


            if (student.getRightFinger() != null || student.getLeftFinger() != null) {
                ImageLoadCallback callback = new ImageLoadCallback() {
                    @Override
                    public void onImageLoaded(byte[] imageData) {
                        // Perform fingerprint matching using the loaded imageData
                        try {
                            boolean leftMatch = matchFingerprints(scannedFingerprint, imageData);
                            boolean rightMatch = matchFingerprints(scannedFingerprint, imageData);

                            progressBar.setVisibility(View.GONE);

                            if (leftMatch || rightMatch) {
                                boolean courseEligibility = false;
                                Intent intent = new Intent(ExamsScanActivity.this, ExamsDialogBoxActivity.class);
                                intent.putExtra("studentName", student.getStudentName());
                                intent.putExtra("matricNo", student.getStudentNumber());
                                intent.putExtra("img", student.getProductImg());
                                intent.putExtra("userId", student.getUserID());
                                intent.putExtra("examsName",examsName);
                                intent.putExtra("examsTime", examsTime);
                                intent.putExtra("examsEndTime", examsEndTime);
                                for (String courses : student.getCourses()){
                                    if(courses.equalsIgnoreCase(examsName)){
                                        courseEligibility = true;
                                    }else {
                                        courseEligibility = false;
                                    }
                                }
                                intent.putExtra("courseEligibility", courseEligibility);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ExamsScanActivity.this, "Fingerprint not recognized in exams.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onImageLoadFailed(Exception e) {
                        // Handle the case where image loading failed
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ExamsScanActivity.this, "Failed to load image from URL.", Toast.LENGTH_SHORT).show();
                    }
                };

                String fingerUrlToMatch = student.getRightFinger() != null ? student.getRightFinger() : student.getLeftFinger();
                loadImageFromUrl(fingerUrlToMatch, callback);
            }


        }
    }

}