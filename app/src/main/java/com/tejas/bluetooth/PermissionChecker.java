package com.tejas.bluetooth;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// PermissionChecker class handles the management of app permissions related to audio recording, storage, and location.
public class PermissionChecker {

    // Constants for permissions request code and the list of required permissions.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    // Context reference to interact with the Android system.
    private Context context;

    // Constructor to initialize the context.
    public PermissionChecker(Context context) {
        this.context = context;
    }

    // Method to request the required permissions.
    public void requestPermissions() {
        // Request all permissions at once
        ActivityCompat.requestPermissions((AppCompatActivity) context, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }

    // Method to check if all required permissions are granted.
    // If not, it requests the permissions.
    public boolean checkPermissions() {
        // Flag to track if all required permissions are granted.
        boolean allPermissionsGranted = true;

        // Check all required permissions
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // If any permission is not granted, set the flag to false.
                allPermissionsGranted = false;
                break;
            }
        }

        // If not all permissions are granted, request them.
        if (!allPermissionsGranted) {
            requestPermissions();
        }

        // Return whether all required permissions are granted.
        return allPermissionsGranted;
    }

}
