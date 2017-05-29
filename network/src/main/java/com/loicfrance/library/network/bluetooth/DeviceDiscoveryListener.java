package com.loicfrance.library.network.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface DeviceDiscoveryListener {
    void deviceDiscovered(BluetoothDevice device);

    void discoveryFinished();
}
