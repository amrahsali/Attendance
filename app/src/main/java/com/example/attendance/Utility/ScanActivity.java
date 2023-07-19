package com.example.attendance.Utility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.LoginModule.MainActivity;
import com.example.attendance.R;
import com.example.attendance.StaffModule.StaffRVModal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

import asia.kanopi.uareu4500library.Status;

public class ScanActivity extends AppCompatActivity {
    ImageView print;
    TextView statusText;
    int click = 1;
    Fingerprint fingerprint;
    private ScanUtils scanUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        print = findViewById(R.id.print_image);
        statusText = findViewById(R.id.status);
        fingerprint = new Fingerprint();
        scanUtils = new ScanUtils(ScanActivity.this, print, print, statusText);

        print.setOnClickListener(v -> {
            byte[] leftBmpData = scanUtils.getLeftBmpData();
            compareFingerprint(leftBmpData);
        });
    }

    @Override
    protected void onStop() {
        scanUtils.stopScan();
        super.onStop();
    }

    @Override
    protected void onStart() {
        scanUtils.scan(ScanActivity.this);
        super.onStart();
    }

    // Method to perform fingerprint matching
    private boolean matchFingerprints(byte[] probe, byte[] candidate) {
        FingerprintTemplate probeTemplate = new FingerprintTemplate(probe);
        FingerprintTemplate candidateTemplate = new FingerprintTemplate(candidate);

        FingerprintMatcher matcher = new FingerprintMatcher()
                .index(probeTemplate);

        double score = matcher.match(candidateTemplate);
        if (score >= 5.0) {
            // Found a match
            return true;
        } else {
            // No match
            return false;
        }
    }

    private void compareFingerprint(byte[] scannedFingerprint) {
        FirebaseDatabase.getInstance().getReference().child("Staff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot staffSnapshot : snapshot.getChildren()) {
                            StaffRVModal staff = staffSnapshot.getValue(StaffRVModal.class);
                            if (staff != null) {

                                byte[] storedRightFingerprint = staff.getRightFinger().getBytes();
                                byte[] storedLeftFingerprint = staff.getLeftFinger().getBytes();

                                // Perform fingerprint matching
                                boolean leftMatch = matchFingerprints(scannedFingerprint, storedLeftFingerprint);
                                boolean rightMatch = matchFingerprints(scannedFingerprint, storedRightFingerprint);

                                if (leftMatch || rightMatch) {
                                    // Fingerprint matched, perform your action here
                                    // For example, redirect to a success activity
                                    Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    return;
                                }
                            }
                        }

                        // No matching fingerprint found, perform your action here
                        // For example, show an error message
                        Toast.makeText(ScanActivity.this, "Fingerprint not recognized.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error occurred while fetching staff data
                        Toast.makeText(ScanActivity.this, "Failed to retrieve staff data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
