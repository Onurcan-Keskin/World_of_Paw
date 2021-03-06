package com.example.wop.BLE_Model;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import java.util.List;

import androidx.annotation.RequiresApi;

public class WoPGatt implements BluetoothGattRepresentation {
    private BluetoothGatt gatt;

    public WoPGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    @Override
    public void close() {
        gatt.close();
    }

    @Override
    public boolean connect() {
        return gatt.connect();
    }

    @Override
    public void disconnect() {
        gatt.disconnect();
    }

    @Override
    public boolean discoverServices() {
        return gatt.discoverServices();
    }

    @Override
    public List<BluetoothGattService> getServices() {
        return gatt.getServices();
    }

    @Override
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gatt.readCharacteristic(characteristic);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void readPhy() {
        gatt.readPhy();
    }

    @Override
    public boolean readRemoteRssi() {
        return gatt.readRemoteRssi();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestConnectionPriority(int connectionParameter) {
        return gatt.requestConnectionPriority(connectionParameter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean requestMtu(int size) {
        return gatt.requestMtu(size);
    }

    @Override
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        return gatt.setCharacteristicNotification(characteristic, enable);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setPreferredPhy(int txPhy, int rxPhy, int phyOptions) {
        gatt.setPreferredPhy(txPhy, rxPhy, phyOptions);
    }

    @Override
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        return gatt.writeCharacteristic(characteristic);
    }

    @Override
    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        return gatt.writeDescriptor(descriptor);
    }
}
