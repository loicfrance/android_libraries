package com.loicfrance.library.network.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.loicfrance.library.network.NetworkThread;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Loic France on 22/06/2015.
 */
public class BluetoothServerNetworkThread extends NetworkThread {

    private final BluetoothServerSocket serverSock;
    private BluetoothSocket clientSocket;

    public BluetoothServerNetworkThread(String name, String uuid, Handler inputHandler, int requestId_offset) {
        super(inputHandler, requestId_offset);

        BluetoothServerSocket tmp;
        try {
            tmp = BluetoothManager.bluetooth
                    .listenUsingInsecureRfcommWithServiceRecord(name, UUID.fromString(uuid));
        } catch (IOException e) {
            tmp = null;
        }
        serverSock = tmp;
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
    /**
     * this thread keeps listening until a socket is returned or an Exception occurs.
     * If a socket is returned, it starts the connection between them.
     */
    @Override
    public void run() {

        //keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                //LogD.d("BT_CONNECTION(S)", "waiting for connection (socket: " + serverSock);
                //TODO ask an interface if the app wants to accept the connection
                clientSocket = serverSock.accept();
            } catch (IOException e) {
                break;
            }
            //if  connection was accepted
            if (clientSocket != null) {
                //LogD.d("BT_CONNECTION(S)", "connection detected");
                //let the superclass handle the communication
                init();
                process();
            }
            if(!this.isRunning()) break;
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            serverSock.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
