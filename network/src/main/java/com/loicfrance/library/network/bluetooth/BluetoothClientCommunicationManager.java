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

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.RequiresPermission;

import com.loicfrance.library.network.CommunicationListener;
import com.loicfrance.library.network.CommunicationManager;
import com.loicfrance.library.network.StreamCommunicationManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Loic France on 22/06/2015.
 * This class is the bluetooth client implementation of the {@link CommunicationManager} class.
 */
public class BluetoothClientCommunicationManager extends StreamCommunicationManager {

    private UUID uuid;
    private BluetoothDevice server;
    private BluetoothSocket clientSocket;

    public BluetoothClientCommunicationManager(String uuid, CommunicationListener listener,
                                               BluetoothDevice serverDevice) {
        super("Thread-BT-"+serverDevice.getAddress(), listener);
        this.uuid = UUID.fromString(uuid);
        this.server = serverDevice;
    }
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void start() throws IOException {
        super.start();
        this.getNetworkThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    //build bluetooth socket with the server device and the UUID
                    clientSocket = server.createRfcommSocketToServiceRecord(uuid);
                    setInputStream(clientSocket.getInputStream());
                    setOutputStream(clientSocket.getOutputStream());
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
                        BluetoothClientCommunicationManager.this.stop(false);
                    }
                } catch (/*InterruptedException |*/ IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
}
