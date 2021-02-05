package com.example.juggler;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

public class SerialSocket implements Runnable{
    //UUID
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //Context
    private Context context;
    //Bluetooth Device
    private final BluetoothDevice device;
    //Bluetooth Socket
    private BluetoothSocket socket;


    public SerialSocket(Context context, BluetoothDevice device){
        this.context = context;
        this.device = device;
    }

    public void connect() throws IOException{
        Executors.newSingleThreadExecutor().submit(this);

    }

    public void disconnect(){
        if (socket != null){
            try{
                socket.close();
            }catch (Exception e){

            }
            socket = null;
        }
    }

    public void write(byte[] data) throws IOException{
        socket.getOutputStream().write(data);
    }

    @Override
    public void run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            socket = null;
            return;
        }

        try{
            byte[] buffer = new byte[1024];
            int len;

            while (true){
                len = socket.getInputStream().read(buffer);
                byte[] data = Arrays.copyOf(buffer, len);
            }
        }catch (Exception e){
            try{
                socket.close();
            }catch (Exception ignored){

            }
            socket = null;
        }
    }

    public void cancel(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
