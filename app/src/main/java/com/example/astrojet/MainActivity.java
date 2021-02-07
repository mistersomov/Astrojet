package com.example.astrojet;


import java.io.IOException;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    //UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice remoteDevice;
    private LinearLayout bluetoothLayout;
    private BluetoothService bluetoothService;

    private MaterialCardView cameraCard, chartsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLayout = findViewById(R.id.bluetooth_ll);
        remoteDevice = null;

        bluetoothService = new BluetoothService(this, bluetoothAdapter, bluetoothLayout);
        bluetoothLayout.addView(bluetoothService.setView());

        cameraCard = findViewById(R.id.camera_card);
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        chartsCard = findViewById(R.id.charts_card);
        chartsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChartsActivity.class);
                startActivity(intent);
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int extraBond = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    if (extraBond == BluetoothDevice.BOND_NONE){
                        bluetoothLayout.addView(bluetoothService.update());
                        Toast.makeText(
                                MainActivity.this,
                                "Disconnected",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bluetoothLayout.addView(bluetoothService.update());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    private class ClientSocket extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientSocket(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mmDevice = device;

            try{
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (Exception e){
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run(){
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}