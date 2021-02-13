package com.example.astrojet;

import android.bluetooth.BluetoothDevice;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DataHolder {

    private Map<String, WeakReference<BluetoothDevice>> data = new HashMap<String, WeakReference<BluetoothDevice>>();
    public boolean isSaved;
    private static final DataHolder HOLDER = new DataHolder();

    public static DataHolder getInstance(){
        return HOLDER;
    }

    public void saveData(String tag, BluetoothDevice object){
        data.put(tag, new WeakReference<BluetoothDevice>(object));
        isSaved = true;
    }

    public Object loadData(String tag){
        WeakReference<BluetoothDevice> objectWeakReference = data.get(tag);

        return objectWeakReference.get();
    }
}
