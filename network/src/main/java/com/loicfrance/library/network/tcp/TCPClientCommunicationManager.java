package com.loicfrance.library.network.tcp;

import com.loicfrance.library.network.CommunicationListener;
import com.loicfrance.library.network.StreamCommunicationManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Loic France on 05/06/2015.
 */
public class TCPClientCommunicationManager extends StreamCommunicationManager {

    private String IPAddress;
    private int port;
    private Socket clientSocket;

    public TCPClientCommunicationManager(String IPAddress, int port, CommunicationListener listener) {
        super("Thread-TCP:"+IPAddress+":"+port, listener);
        this.IPAddress = IPAddress;
        this.port = port;
    }

    @Override
    public void start() throws IOException {
        super.start();
        this.getNetworkThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(InetAddress.getByName(IPAddress), port);
                    setInputStream(clientSocket.getInputStream());
                    setOutputStream(clientSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    clientSocket = null;
                }
            }
        });
    }

    public void stop(boolean safeQuit) {
        super.stop(safeQuit);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
