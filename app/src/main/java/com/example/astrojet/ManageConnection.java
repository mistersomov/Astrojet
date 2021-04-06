package com.example.astrojet;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.util.UUID;

public class ManageConnection implements Runnable, ConnectionStatusListener{

    private Activity context;
    private BluetoothDevice device;
    protected static BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "ManageConnection";
    protected static MaterialTextView socketStatus;
    protected static ImageView socketStatusIcon;
    private BluetoothService bluetoothService;
    protected static boolean connected, isFlag;

    public ManageConnection(Activity context, BluetoothService bluetoothService){
        this.context = context;
        this.bluetoothService = bluetoothService;
        socketStatus = context.findViewById(R.id.socket_status);
        socketStatusIcon = context.findViewById(R.id.socket_status_icon);
    }

    public void write(byte[] data){
        try {
            if (socket != null){
                socket.getOutputStream().write(data);
            }
        } catch (IOException e) {
            connectLost();
        }
    }

    public void setSocket(){
        if (connected){
            return;
        }else if (!connected && DataHolder.getInstance().isSaved){
            device = (BluetoothDevice) DataHolder.getInstance().loadData(DeviceActivity.REM_DEV);
            run();
            return;
        }else if (!connected && !DataHolder.getInstance().isSaved){
            for (BluetoothDevice i : bluetoothService.pairedDevices){
                device = i;
                run();
                return;
            }
        }else{
            return;
        }
    }

    @Override
    public void run() {
        socketStatus.setVisibility(View.VISIBLE);
        socketStatusIcon.setVisibility(View.VISIBLE);
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            if (socket.isConnected()){
                connected = true;
                isFlag = false;
                socketStatus.setText(R.string.socket);
                socketStatusIcon.setImageResource(R.drawable.ic_baseline_check_circle_24);


            }
        } catch (IOException connectException) {
            connectLost();

            return;
        }
    }

    @Override
    public void connectLost() {
        try {
            socket.close();
            connected = false;
            socketStatus.setText(R.string.socket);
            socketStatusIcon.setImageResource(R.drawable.ic_baseline_cancel_24);
            if (!isFlag){
                Toast.makeText(
                        context,
                        "Socket is closed",
                        Toast.LENGTH_SHORT
                ).show();
                isFlag = true;
            }

        }catch (IOException closeException){
            Log.e(TAG, "Could not close the client socket", closeException);
        }
    }
}
