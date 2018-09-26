/*
 * Copyright 2018 RichardFrance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

import java.util.List;
import java.util.UUID;

/**
 * Created by Loic France on 08/12/2017.
 * Simple Bluetooth Low Energy Communication Handler using GATT Profile.
 * Uses a separate thread to maintain compatibility with parent class and other communications, but
 * a single thread would have been enough, as the communication is handled separately by the device.
 *<pre>
 * Example :
 * {@code
 *
 * BLEGattClientCommunicationManager bleManager = new BLEGattClientCommunicationManager(context,
 *      device,
 *      new CommunicationListener() {
 *          public void onData(CommunicationManager cm, byte[] data) {
 *              //first parameter is bleManager. Second parameter is the data sent by the BLE server.
 *              //this method is called when the android device receives information
 *              //from the opposite device.
 *          }
 *          @Override
 *          public void onCommStart(CommunicationManager cm) {
 *              //directly called inside the start() method
 *          }
 *          @Overrride
 *          public void onCommEnd(CommunicationManager cm) {
 *              //directly called inside the stop() method
 *          }
 *      }
 * );
 * bleManager.start(); //connect to the opposite device
 * }
 * </pre>
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEGattClientCommunicationManager extends CommunicationManager{

    public interface BLEGattListener extends CommunicationListener {
        void onServicesDiscovered(List<BluetoothGattService> services);
    }

    private BluetoothGatt gatt;
    private BluetoothGattService writeService;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    private BluetoothGattCharacteristic readCharacteristic = null;
    private BluetoothDevice device;
    private Context context;

    private final static int MSG_SERVICES_DISCOVERED = 1;


    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            getCallingThreadHandler().obtainMessage(MSG_SERVICES_DISCOVERED).sendToTarget();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            readCharacteristic = characteristic;
            BLEGattClientCommunicationManager.super.onInputData(characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    public BLEGattClientCommunicationManager(Context context, BluetoothDevice device, @NonNull BLEGattListener listener) {
        super("Thread-BLE", listener);
        this.context = context;
        this.device = device;
    }
    @Override
    public void start() {
        gatt = this.device.connectGatt(context, false, gattCallback);
    }

    @Override
    public void stop(boolean safeQuit) {
        gatt.disconnect();
        super.stop(safeQuit);
    }
    @Override
    protected boolean onLocalInputData(int what, Object obj) {
        if(super.onLocalInputData(what, obj)) return true;
        else if(what==MSG_SERVICES_DISCOVERED) {
            ((BLEGattListener)getCommunicationListener()).onServicesDiscovered(gatt.getServices());
            return true;
        } else return false;
    }

    public boolean setWriteService(UUID uuid) {
        writeService = gatt.getService(uuid);
        return writeService != null;
    }

    public boolean setWriteCharacteristic(UUID uuid) {
        if(writeService != null) {
            writeCharacteristic = writeService.getCharacteristic(uuid);
            return writeCharacteristic != null;
        }
        else return false;
    }

    public BluetoothGattCharacteristic getReadCharacteristic() {
        return this.readCharacteristic;
    }
    public void discoverServices() {
        gatt.discoverServices();
    }

    @Override
    protected void onOutputData(byte[] bytes) {
        writeCharacteristic.setValue(bytes);
        gatt.writeCharacteristic(writeCharacteristic);
    }
}
