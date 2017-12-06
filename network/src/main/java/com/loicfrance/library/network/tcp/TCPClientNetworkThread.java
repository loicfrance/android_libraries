package com.loicfrance.library.network.tcp;

import android.os.Handler;

import com.loicfrance.library.network.NetworkThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Loic France on 05/06/2015.
 */
public class TCPClientNetworkThread extends NetworkThread {

    private String IPAddress;
    private int port;
    private Socket clientSocket;

    public TCPClientNetworkThread(String IPAddress, int port, Handler inputHandler, int requestId_offset) {
        super(inputHandler, requestId_offset);
        this.IPAddress = IPAddress;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(InetAddress.getByName(IPAddress), port);
        } catch (Exception e) {
            //LogD.e("TCP_CLIENT", "cannot build socket with IP address : " + IPAddress + " and port " + port + "");
            e.printStackTrace();
            clientSocket = null;
        }
        if (clientSocket != null) {
            init();
            process();
        }
    }
    protected void init() {
        try {
            //get the in and out streams
            init(clientSocket.getInputStream(), clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        super.close();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
