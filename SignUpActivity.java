package com.termproject.ac.barcodescanner;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.termproject.ac.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.CAMERA;

public class BarcodeScannerActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private DecoratedBarcodeView barcodeScannerView;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner_activity);

        // Initialize the barcode scanner view
        barcodeScannerView = findViewById(R.id.barcode_scanner_view);

        // Request camera permission
        requestCameraPermission();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startScanning();
        }
    }

    private void startScanning() {
        // Start the scanner
        barcodeScannerView.resume();
        barcodeScannerView.decodeContinuous(result -> {
            if (result.getText() != null) {
                // Create an intent to hold the scanned data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("scannedData", result.getText());
                // Set the result to be OK and pass the intent
                setResult(RESULT_OK, resultIntent);


                // Create and show the AlertDialog only if the activity is still active
                if (!isFinishing() && !isDestroyed()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Attendace Check ✔")
                            .setMessage(result.getText());

                    // Create the AlertDialog and show it
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Create a Handler to dismiss the dialog and return to AttendanceListActivity after 2 seconds
                    new Handler().postDelayed(() -> {
                        // Dismiss the dialog and finish the current activity only if still valid
                        if (!isFinishing() && !isDestroyed()) {
                            dialog.dismiss();
                            finish();
                        }
                    }, 2000);
                }


            }

        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission is required for scanning.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
