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
