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
