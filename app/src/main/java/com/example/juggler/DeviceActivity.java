package com.example.juggler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<DeviceItem> bluetoothDevices = new ArrayList<DeviceItem>();

    private BroadcastReceiver broadcastReceiver;

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public boolean isFlag;

    private SwipeRefreshLayout swipeRefreshLayout;

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
                ExampleThread exampleThread = new ExampleThread(position);
                new Thread(exampleThread).start();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                            bluetoothAdapter.startDiscovery();
                            break;
                        }
                        break;

                    case BluetoothDevice.ACTION_FOUND:
                        if (!isFlag){
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if (device.getName() != null) {
                                bluetoothDevices.add(new DeviceItem(device.getName(), device.getAddress()));
                            } else {
                                bluetoothDevices.add(new DeviceItem("Unnamed", device.getAddress()));
                            }
                            adapter.notifyDataSetChanged();
                            break;
                        }

                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        startActivityForResult(new Intent(DeviceActivity.this, MainActivity.class), 1);
                }
            }
        };

        swipeRefreshLayout = findViewById(R.id.device_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bluetoothAdapter.cancelDiscovery();
                bluetoothDevices.clear();
                bluetoothAdapter.startDiscovery();

                swipeRefreshLayout.setRefreshing(false);
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
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }else{
            bluetoothAdapter.startDiscovery();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(broadcastReceiver);

    }

    private class ExampleThread implements Runnable{
        int position;

        public ExampleThread(int position){
            this.position = position;
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();
            String address = bluetoothDevices.get(position).getDeviceAddress();
            bluetoothAdapter.getRemoteDevice(address).createBond();
        }
    }
}