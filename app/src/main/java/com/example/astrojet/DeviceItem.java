package com.example.astrojet;

public class DeviceItem {

    private String deviceName, deviceAddress;

    public DeviceItem(String deviceName, String deviceAddress){
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }


    public String getDeviceName(){
        return deviceName;
    }

    public String getDeviceAddress(){
        return deviceAddress;
    }

}
