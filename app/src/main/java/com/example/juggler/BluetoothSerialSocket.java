package com.example.juggler;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.UUID;

public class BluetoothSerialSocket extends Thread {

    private static final UUID MY_UUID = UUID.fromString("253B3656-C915-4E6C-9021-DAFCBF942A0F");
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice device;
    private final BluetoothSocket socket;

    public BluetoothSerialSocket(Context context, BluetoothAdapter bluetoothAdapter, BluetoothDevice remDevice){
        if (context instanceof Activity){
            throw new InvalidParameterException("Expected a non UI context");
        }
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = remDevice;

        BluetoothSocket tmp = null;
        try {
            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;

    }

    public void write(int bytes){
        try {
            socket.getOutputStream().write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        bluetoothAdapter.cancelDiscovery();

        try{
            socket.connect();
        }catch (IOException connectException){
            try{
                socket.close();
            }catch (IOException closeException){
                Log.e("TAG", "Could not close the client socket", closeException);
            }
            return;
        }
    }

    public void cancel(){
        try{
            socket.close();
        }catch (IOException e){
            Log.e("TAG", "Could not close the client socket", e);
        }

    }


}
