package com.loicfrance.library.network.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.loicfrance.library.network.CommunicationListener;
import com.loicfrance.library.network.CommunicationManager;

import java.util.UUID;

/**
 * Created by Loic France on 08/12/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLEClientCommunicationManager extends CommunicationManager{

    private BluetoothGatt gatt;
    BluetoothGattService writeService;
    BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothDevice device;
    private Context context;


    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    public BluetoothLEClientCommunicationManager(Context context, BluetoothDevice device, @NonNull CommunicationListener listener) {
        super("Thread-BLE", listener);
        this.context = context;
        this.device = device;
    }
    @Override
    public void start() {
        gatt = this.device.connectGatt(context, false, gattCallback);
    }
    public boolean setWriteService(UUID uuid) {
        writeService = gatt.getService(uuid);
        return writeService != null;
    }
    public void setWriteCharacteristic(UUID uuid) {
        if(writeService != null)
            writeCharacteristic = writeService.getCharacteristic(uuid);
    }

    @Override
    protected void onOutputData(byte[] bytes) {
        writeCharacteristic.setValue(bytes);
        gatt.writeCharacteristic(writeCharacteristic);
    }
}
