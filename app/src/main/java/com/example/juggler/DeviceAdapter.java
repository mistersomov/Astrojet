package com.example.juggler;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceAdapterHolder> {

    private ArrayList<DeviceItem> deviceList;
    private OnClickListener mListener;

    public interface OnClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnClickListener listener){
        mListener = listener;
    }

    public static class DeviceAdapterHolder extends RecyclerView.ViewHolder{
        public MaterialTextView deviceName, deviceAddress;

        public DeviceAdapterHolder(@NonNull View itemView, final OnClickListener listener) {
            super(itemView);

            deviceName = itemView.findViewById(R.id.device_text1);
            deviceAddress = itemView.findViewById(R.id.device_text2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public DeviceAdapter(ArrayList<DeviceItem> deviceList){
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        DeviceAdapterHolder deviceAdapterHolder = new DeviceAdapterHolder(v, mListener);

        return deviceAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapterHolder holder, int position) {

        DeviceItem currentDevice = deviceList.get(position);

        holder.deviceName.setText(currentDevice.getDeviceName());
        holder.deviceAddress.setText(currentDevice.getDeviceAddress());

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
}
