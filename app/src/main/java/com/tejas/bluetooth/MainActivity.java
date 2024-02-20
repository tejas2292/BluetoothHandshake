package com.tejas.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private TextView statusTextView;
    BroadcastReceiver mBrodcastReceiver;
    ListView deviceListView;

    private static final long SCAN_INTERVAL = 60000; // Scan interval in milliseconds (e.g., every 60 seconds)
    private Handler mHandler = new Handler();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceListView = findViewById(R.id.deviceListView);
        statusTextView = findViewById(R.id.statusTextView);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Make the current device discoverable to others
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);

        if (bluetoothAdapter == null) {
            statusTextView.setText("Bluetooth is not supported on this device");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 3);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 5);
        }

        // Start periodic scanning
        mHandler.post(scanRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop periodic scanning
        mHandler.removeCallbacks(scanRunnable);
    }

    // Runnable for periodic scanning
    private Runnable scanRunnable = new Runnable() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            bluetoothAdapter.startDiscovery();

            if (mBrodcastReceiver != null) {
                unregisterReceiver(mBrodcastReceiver);
            }

            mHandler.postDelayed(this, SCAN_INTERVAL);

            ArrayList<String> arrayList = new ArrayList<>();

            mBrodcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();
                    if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null && device.getName() != null) { // Add null check
                            Log.i("BLUETOOTH DEVICES", device.getName());
                            arrayList.add("Name: " + device.getName() + ", MAC Address: " + device.getAddress());
                        }
                    }

                    if (arrayList.size() != 0) {
                        statusTextView.setText("Total " + arrayList.size() + " Devices Found");
                        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_list_item_1, arrayList);
                        deviceListView.setAdapter(itemAdapter);
                    } else {
                        statusTextView.setText("No Devices Found");
                    }

                }
            };

            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBrodcastReceiver, intentFilter);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(scanRunnable);
        // Unregister the broadcast receiver when the activity is destroyed
        unregisterReceiver(mBrodcastReceiver);
    }
}
