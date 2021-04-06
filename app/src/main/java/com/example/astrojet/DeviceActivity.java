package com.example.astrojet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

public class DeviceActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<DeviceItem> bluetoothDevices = new ArrayList<DeviceItem>();
    private BluetoothDevice remDev;

    private BroadcastReceiver broadcastReceiver;

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isRestarted;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String REM_DEV = "com.example.astrojet.REMOTE_DEVICE";
    private static final String TAG = "DeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        recyclerView = findViewById(R.id.device_recyclerview);
        adapter = new DeviceAdapter(bluetoothDevices);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                BondThread bondThread = new BondThread(position);
                new Thread(bondThread).start();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                            refresh();
                        }else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                            bluetoothAdapter.cancelDiscovery();
                        }
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        if (!isRestarted){
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device.getName() != null) {
                                bluetoothDevices.add(new DeviceItem(device.getName(), device.getAddress()));
                            } else {
                                bluetoothDevices.add(new DeviceItem("Unnamed", device.getAddress()));
                            }
                            adapter.notifyDataSetChanged();
                            break;
                        }

                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        int extraBond = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                        if (extraBond == BluetoothDevice.BOND_BONDED){
                            DataHolder.getInstance().saveData(REM_DEV, remDev);
                            //startActivityForResult(new Intent(DeviceActivity.this, MainActivity.class),RESULT_OK);
                            finish();
                        }
                        break;
                }
            }
        };

        swipeRefreshLayout = findViewById(R.id.device_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (bluetoothAdapter == null){
            Toast.makeText(
                    getApplicationContext(),
                    "Device doesn't support Bluetooth",
                    Toast.LENGTH_SHORT
            ).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        if (!isRestarted) {
            bluetoothAdapter.startDiscovery();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRestarted = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(broadcastReceiver);

    }

    public void refresh(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }else{
            bluetoothAdapter.cancelDiscovery();
            bluetoothDevices.clear();
            adapter.notifyDataSetChanged();
            isRestarted = false;
            bluetoothAdapter.startDiscovery();
            
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class BondThread implements Runnable{
        private int position;

        public BondThread(int position){
            this.position = position;
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            String address = bluetoothDevices.get(position).getDeviceAddress();
            remDev = bluetoothAdapter.getRemoteDevice(address);
            remDev.createBond();
        }
    }
}