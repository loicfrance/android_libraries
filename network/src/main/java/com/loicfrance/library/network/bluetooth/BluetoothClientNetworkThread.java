package com.loicfrance.library.network.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.annotation.RequiresPermission;

import com.loicfrance.library.network.NetworkThread;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Loic France on 22/06/2015.
 * This class is the bluetooth client implementation of the {@link NetworkThread} class.
 */
public class BluetoothClientNetworkThread extends NetworkThread {

    private UUID uuid;
    private BluetoothDevice server;
    private BluetoothSocket clientSocket;

    /**
     *
     * @param uuid
     * @param inputHandler will receive the incoming data, and communication state information
     * @param serverDevice device to connect to
     */
    public BluetoothClientNetworkThread(String uuid, Handler inputHandler, BluetoothDevice serverDevice,
                                        int requestId_offset) {
        super(inputHandler, requestId_offset);
        this.uuid = UUID.fromString(uuid);
        this.server = serverDevice;
    }

    protected void init() {
        try {
            //connect to the device
            clientSocket.connect();
            //get the in and out streams
            init(clientSocket.getInputStream(), clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void run() {
        try {

            //build bluetooth socket with the server device and the UUID
            clientSocket = server.createRfcommSocketToServiceRecord(uuid);
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                //LogD.d("BT_CONNECTION(C)", "connecting to server : socket : " + clientSocket);
                clientSocket.connect();
                //LogD.d("BT_CONNECTION(C)", "connected to server");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    clientSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
        } catch (/*InterruptedException |*/ IOException e) {
            e.printStackTrace();
            return;
        }

        //and finally, let the superclass handle the communication
        init();
        process();
    }


}
