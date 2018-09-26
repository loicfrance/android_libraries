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

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.loicfrance.library.network.CommunicationListener;
import com.loicfrance.library.network.StreamCommunicationManager;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Loic France on 22/06/2015.
 */
public class BluetoothServerCommunicationManager extends StreamCommunicationManager {

    private UUID uuid;
    private String name;
    private BluetoothServerSocket serverSock;
    private BluetoothSocket clientSocket;

    public BluetoothServerCommunicationManager(String name, String uuid, CommunicationListener listener) {
        super("Thread-BT-server:"+name, listener);
        this.uuid = UUID.fromString(uuid);
        this.name = name;
    }
    @Override
    public void start() throws IOException {
        super.start();
        this.getNetworkThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSock = BluetoothManager.getAdapter()
                            .listenUsingInsecureRfcommWithServiceRecord(name, uuid);

                    listenIncommingConnection();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void listenIncommingConnection() throws IOException {
        //TODO ask an interface if the app wants to accept the connection
        clientSocket = serverSock.accept();
        if(clientSocket != null) {
            setInputStream(clientSocket.getInputStream());
            setOutputStream(clientSocket.getOutputStream());
        }
    }

    @Override
    public void stop(boolean safeQuit) {
        super.stop(safeQuit);
        try {
            serverSock.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
