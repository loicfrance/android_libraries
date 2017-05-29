package com.loicfrance.library.network.tcp;

import android.os.Handler;

import com.loicfrance.library.utils.LogD;
import com.loicfrance.library.network.NetworkThread;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Loic France on 05/06/2015.
 */
public class TCPClientNetworkThread extends NetworkThread {

    private String IPAddress;
    private int port;
    private Socket clientSocket;

    public TCPClientNetworkThread(String IPAddress, int port, Handler inputHandler) {
        super(inputHandler);
        this.IPAddress = IPAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            LogD.d("TCP_CLIENT", "building socket...");
            clientSocket = new Socket(InetAddress.getByName(IPAddress), port);
            LogD.d("TCP_CLIENT", "socket build.");
        } catch (Exception e) {
            LogD.e("TCP_CLIENT", "cannot build socket with IP address : " +
                    IPAddress + " and port " + port + "");
            e.printStackTrace();
            clientSocket = null;
        }
        if (clientSocket != null) {
            init(clientSocket);
            process();
        }
    }

}
