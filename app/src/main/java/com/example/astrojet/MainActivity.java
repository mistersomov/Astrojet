package com.example.astrojet;


import java.io.IOException;
import java.util.UUID;

import androidx.annotation.UiThread;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private LinearLayout bluetoothLayout;
    private BluetoothService bluetoothService;
    private SwitchMaterial powerSwitch, ledSwitch;

    private MaterialCardView cameraCard, chartsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLayout = findViewById(R.id.bluetooth_ll);

        bluetoothService = new BluetoothService(this, bluetoothAdapter, bluetoothLayout);
        bluetoothLayout.addView(bluetoothService.setView());

        powerSwitch = findViewById(R.id.service_switch);
        ledSwitch = findViewById(R.id.led_switch);

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
                    if (extraBond == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(
                                MainActivity.this,
                                "Connected",
                                Toast.LENGTH_SHORT
                        ).show();
                    }else if (extraBond == BluetoothDevice.BOND_NONE){
                        try {
                            bluetoothLayout.addView(bluetoothService.update());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    bluetoothService.send(MessageConstants.LED_ON);
                }else{
                    bluetoothService.send(MessageConstants.LED_OFF);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            bluetoothLayout.addView(bluetoothService.update());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}