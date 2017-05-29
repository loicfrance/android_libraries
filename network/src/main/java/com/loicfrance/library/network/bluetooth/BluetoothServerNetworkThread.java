package com.loicfrance.library.network.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.loicfrance.library.utils.LogD;
import com.loicfrance.library.network.NetworkThread;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by Loic France on 22/06/2015.
 */
public class BluetoothServerNetworkThread extends NetworkThread {

    private final BluetoothServerSocket serverSock;

    public BluetoothServerNetworkThread(Activity context, String name, String uuid,
                                        Handler inputHandler) {
        super(inputHandler);

        BluetoothServerSocket tmp;
        try {
            tmp = BluetoothManager.bluetooth
                    .listenUsingInsecureRfcommWithServiceRecord(name, UUID.fromString(uuid));
        } catch (IOException e) {
            tmp = null;
        }
        serverSock = tmp;
    }

    /**
     * this thread keeps listening until a socket is returned or an Exception occurs.
     * If a socket is returned, it starts the connection between them.
     */
    @Override
    public void run() {
        BluetoothSocket socket = null;

        //keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                LogD.d("BT_CONNECTION(S)", "waiting for connection (socket: " + serverSock);
                //TODO ask an interface if the app wants to accept the connection
                socket = serverSock.accept();
            } catch (IOException e) {
                break;
            }
            //if  connection was accepted
            if (socket != null) {
                LogD.d("BT_CONNECTION(S)", "connection detected");
                //let the superclass handle the communication
                init(socket);
                process();
            }
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            serverSock.close();
        } catch (IOException e) {
        }
    }
}
