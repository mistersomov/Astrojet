package com.example.astrojet;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BluetoothAdapter bluetoothAdapter;
    private LinearLayout bluetoothLayout;
    private BottomAppBar bottomAppBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BluetoothService bluetoothService;
    private SwitchMaterial powerSwitch, ledSwitch, trackSwitch;
    private ManageConnection manageConnection;

    private MaterialCardView cameraCard, chartsCard;
    protected MaterialButton servoButton, gearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting Theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        findingView();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLayout = findViewById(R.id.bluetooth_ll);

        bluetoothService = new BluetoothService(this, bluetoothAdapter, bluetoothLayout);
        bluetoothLayout.addView(bluetoothService.setView());

        manageConnection = new ManageConnection(this, bluetoothService);
        //manageConnection.setSocket();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                manageConnection.setSocket();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

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
                        try {
                            bluetoothLayout.addView(bluetoothService.update());
                            Toast.makeText(
                                    MainActivity.this,
                                    "Connected",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (extraBond == BluetoothDevice.BOND_NONE){
                        try {
                            bluetoothLayout.addView(bluetoothService.update());
                            Toast.makeText(
                                    MainActivity.this,
                                    "Disconnected",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                try{

                }catch (Exception e){

                }
            }
        });

        ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    String s = "9";
                    manageConnection.write(s.getBytes());
                }else if (!isChecked){
                    String s = "8";
                    manageConnection.write(s.getBytes());
                }
            }
        });

        trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TrackingSystem trackingSystem = new TrackingSystem();
                    trackingSystem.show(getSupportFragmentManager(), "null");
                }else{

                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        manageConnection.setSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void findingView(){
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        swipeRefreshLayout = findViewById(R.id.main_swipe);
        powerSwitch = findViewById(R.id.service_switch);
        ledSwitch = findViewById(R.id.led_switch);
        trackSwitch = findViewById(R.id.track_switch);
        cameraCard = findViewById(R.id.camera_card);
        chartsCard = findViewById(R.id.charts_card);
        servoButton = findViewById(R.id.servo_btn);
        gearButton = findViewById(R.id.gear_btn);
    }
}