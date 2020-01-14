package com.example.wop.BLE_Model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;

import androidx.annotation.RequiresApi;

public class WoPDevice implements BluetoothDeviceRepresentation {
    private BluetoothDevice device;

    public static Creator<WoPDevice> CREATOR = new Creator<WoPDevice>() {

        @Override
        public WoPDevice createFromParcel(Parcel parcel) {
            return new WoPDevice(BluetoothDevice.CREATOR.createFromParcel(parcel));
        }

        @Override
        public WoPDevice[] newArray(int size) {
            return new WoPDevice[size];
        }
    };

    public WoPDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String getAddress() {
        return device.getAddress();
    }

    @Override
    public String getName() {
        return device.getName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public BluetoothGattRepresentation connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback, int transport, int phy) {
        BluetoothGatt gatt = device.connectGatt(context, autoConnect, callback, transport, phy);
        return gatt != null ? new WoPGatt(gatt) : null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public BluetoothGattRepresentation connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback, int transport) {
        BluetoothGatt gatt = device.connectGatt(context, autoConnect, callback, transport);
        return gatt != null ? new WoPGatt(gatt) : null;
    }

    @Override
    public BluetoothGattRepresentation connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback) {
        BluetoothGatt gatt = device.connectGatt(context, autoConnect, callback);
        return gatt != null ? new WoPGatt(gatt) : null;
    }

    @Override
    public int getBondState() {
        return device.getBondState();
    }

    @Override
    public int describeContents() {
        return device.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        device.writeToParcel(parcel, i);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WoPDevice && device.equals(((WoPDevice) o).device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }
}
