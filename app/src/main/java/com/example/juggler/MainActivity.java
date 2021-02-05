package com.example.juggler;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    //UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    boolean connected;
    private LinearLayout bluetoothLayout;

    private MaterialButton selectDevice, disconnectButton;
    private MaterialTextView devName;
    private MaterialCardView cameraCard, chartsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothLayout = findViewById(R.id.bluetooth_ll);

        pairedDevices = bluetoothAdapter.getBondedDevices();

        selectDevice = getLayoutInflater().inflate(R.layout.connect_layout, null).findViewById(R.id.select_device);

        setLayout(bluetoothLayout);

        if (!connected){
            selectDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            disconnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (BluetoothDevice device : pairedDevices){
                        try{
                            Method method = device.getClass().getMethod(
                                    "removeBond",
                                    (Class[]) null);
                            method.invoke(device, (Object[]) null);

                        }catch (Exception e){
                            Log.e("Error", e.getMessage());
                        }
                    }
                }
            });
        }

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

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    }

    public void setLayout(LinearLayout linearLayout){
        View v;
        if (pairedDevices.size() > 0){
            connected = true;

            v = getLayoutInflater().inflate(R.layout.disconnect_layout, null);
            disconnectButton = v.findViewById(R.id.disconnect_btn);
            devName = v.findViewById(R.id.device_name);
            for (BluetoothDevice device : pairedDevices) {
                devName.setText(device.getName());
            }

        }else{
            connected = false;
            v = getLayoutInflater().inflate(R.layout.connect_layout, null);
            selectDevice = v.findViewById(R.id.select_device);
        }
        linearLayout.addView(v);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}