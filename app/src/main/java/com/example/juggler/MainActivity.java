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
    private BluetoothService bluetoothService;

    //private View view;
    //private MaterialButton selectDevice, disconnectButton;
   // private MaterialTextView devName;
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
                        pairedDevices = bluetoothAdapter.getBondedDevices();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothLayout.addView(bluetoothService.update());
                            }
                        });
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
    protected void onResume() {
        super.onResume();
        bluetoothLayout.addView(bluetoothService.update());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}