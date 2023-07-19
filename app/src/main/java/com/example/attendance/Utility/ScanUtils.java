package com.example.attendance.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import asia.kanopi.uareu4500library.Status;

public class ScanUtils {
    private final ImageView leftPrint;
    private final ImageView rightPrint;
    private final TextView statusText;
    private int click = 1;
    private final Fingerprint fingerprint;

    private byte[] leftBmpData;
    private byte[] rightBmpData;


    public ScanUtils(Context context, ImageView leftPrint, ImageView rightPrint, TextView statusText) {
        this.leftPrint = leftPrint;
        this.rightPrint = rightPrint;
        this.statusText = statusText;
        fingerprint = new Fingerprint();
        scan(context);
    }

    public void scan(Context context) {
        fingerprint.scan(context, printHandler, updateHandler);
    }

    public void stopScan() {
        fingerprint.turnOffReader();
    }

    // Getter methods for leftBmpData and rightBmpData
    public byte[] getLeftBmpData() {
        return leftBmpData;
    }

    public byte[] getRightBmpData() {
        return rightBmpData;
    }

    private final Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");
            switch (status) {
                case Status.INITIALISED:
                    statusText.setText("Setting up reader");
                    break;
                case Status.SCANNER_POWERED_ON:
                    statusText.setText("Reader powered on");
                    break;
                case Status.READY_TO_SCAN:
                    statusText.setText("Ready to scan finger");
                    break;
                case Status.FINGER_DETECTED:
                    statusText.setText("Finger detected");
                    break;
                case Status.RECEIVING_IMAGE:
                    statusText.setText("Receiving image");
                    break;
                case Status.FINGER_LIFTED:
                    statusText.setText("Finger has been lifted off reader");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    statusText.setText("Reader is off");
                    break;
                case Status.SUCCESS:
                    statusText.setText("Fingerprint successfully captured");
                    break;
                case Status.ERROR:
                    statusText.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    statusText.setText(String.valueOf(status));
                    break;
            }
        }
    };

    private final Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String errorMessage = "empty";
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status", status);
            if (status == Status.SUCCESS) {
                byte[] image = msg.getData().getByteArray("img");
                if (click % 2 == 0) {
                    Bitmap rightBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    if (rightBitmap != null) {
                        rightBmpData = AndroidBmpUtil.convertToBmp24bit(image);
                        if (rightBmpData != null) {
                            // Save the rightBmpData to the database
                            setRightBmpData(rightBmpData);
                        } else {
                            // Handle BMP conversion failure
                        }
                        rightPrint.setImageBitmap(rightBitmap);
                    } else {
                        // Handle bitmap conversion failure
                    }
                } else {
                    Bitmap leftBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    if (leftBitmap != null) {
                        leftBmpData = AndroidBmpUtil.convertToBmp24bit(image);
                        if (leftBmpData != null) {
                            // Save the leftBmpData to the database
                            setLeftBmpData(leftBmpData);
                        } else {
                            // Handle BMP conversion failure
                        }
                        leftPrint.setImageBitmap(leftBitmap);
                    } else {
                        // Handle bitmap conversion failure
                    }
                }
                click++;
                if (click >= 4) {
                    // Handle the captured left and right images
                    // For example, you can send them to a server for verification
                }
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }

            // Handle the result based on your requirements
            // For example, you can broadcast the intent or use an interface callback
        }
    };

    // Setter methods for leftBmpData and rightBmpData
    private void setLeftBmpData(byte[] leftBmpData) {
        this.leftBmpData = leftBmpData;
    }

    private void setRightBmpData(byte[] rightBmpData) {
        this.rightBmpData = rightBmpData;
    }

}
