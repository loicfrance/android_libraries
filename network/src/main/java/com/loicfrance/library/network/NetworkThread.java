package com.loicfrance.library.network;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.loicfrance.library.utils.LogD;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Loic France on 05/06/2015.
 */
public abstract class NetworkThread extends Thread {
    private static final String LOGTAG = "NET_THREAD";
    public static final int MSG_DATA = 1786804;         //random number constant to all msg
    public static final int MSG_COMM_START = 1786805;   //IDs in package,  + specific id
    public static final int MSG_COMM_STOP = 1786806;
    private DataInputStream in;
    private DataOutputStream out;
    private Handler inputHandler;
    private Socket sock;
    private BluetoothSocket btSock;
    private boolean isRunning = false;
    private int inputMinSize = 1;


    private int inputMaxSize = 100;

    public NetworkThread(@NonNull Handler inputHandler) {
        this.inputHandler = inputHandler;
    }
    public void setInputMinSize(int size) {
        this.inputMinSize = size;
    }
    public int getInputMinSize() {
        return this.inputMinSize;
    }
    public void setInputMaxSize(int inputMaxSize) {
        this.inputMaxSize = inputMaxSize;
    }
    public int getInputMaxSize() {
        return inputMaxSize;
    }

    public void init(Socket socket) {
        LogD.i(LOGTAG, "initialize connection.");
        //put the socket in the private variable
        sock = socket;
        try {
            //get the in and out streams
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            out = null;
            in = null;
        }
    }

    public void init(BluetoothSocket socket) {
        //this second version of the previous function is needed as BluetoothSocket
        //is not a subclass of Socket. this function has the same content, except that
        //the bluetooth socket needs to start the connection using the connect() method.
        LogD.i(LOGTAG, "initialize connection.");
        //put the socket in the private variable
        btSock = socket;
        try {
            //connect to the device
            btSock.connect();
            //get the in and out streams
            in = new DataInputStream(btSock.getInputStream());
            out = new DataOutputStream(btSock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            out = null;
            in = null;
        }
    }

    @Override
    public abstract void run();

    protected void process() {
        LogD.i(LOGTAG, "processing data stream reading.");
        isRunning = true;
        //notify the handler that the communication has started
        Message msg = inputHandler.obtainMessage(MSG_COMM_START);
        inputHandler.sendMessage(msg);

        byte[] input;
        //listen continuously for incoming message, until the input data is null
        while (isRunning) {
            if ((input = readData()) == null) {
                LogD.i(LOGTAG, "no more data coming from the network.");
                isRunning = false;
            } else {
                LogD.i(LOGTAG, "data read (length= " + input.length + ").");
                if (input.length > 0 && inputHandler != null) {
                    msg.what = MSG_DATA;
                    msg.obj = input;
                    inputHandler.sendMessage(msg);
                }
            }
        }
        LogD.i(LOGTAG, "network thread shutting down.");
        try {
            if (sock != null) sock.close();
            else if (btSock != null) btSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //notify the handler that the communication has stopped
        msg.what = MSG_COMM_STOP;
        msg.obj = null;
        inputHandler.sendMessage(msg);
    }

    public void close() {
        isRunning = false;
    }

    private byte[] readData() {
        int len = 0;
        byte[] buffer = new byte[this.inputMaxSize];
        try {
            do {
                len += in.read(buffer, len, this.inputMaxSize-len);
                LogD.i(LOGTAG, "read " + len +" bytes from input stream.");
                if (len < 0) {
                    LogD.e(LOGTAG, "Message Length Error : length = " + len);
                    return null;
                }
            } while(len > 0 && len < this.inputMaxSize && in.available() > 0);
        } catch (Exception e) {
            LogD.e(LOGTAG, "Error reading data : ");
            e.printStackTrace();
            return null;
        }
        return buffer;
    }


    public boolean send(byte[] data) {
        try {
            out.write(data);
            out.flush();
            return true;
        } catch (Exception e) {
            LogD.e(LOGTAG, "Error sending message :");
            e.printStackTrace();
            return false;
        }
    }

    public void setInputHandler(@NonNull Handler inputHandler) {
        this.inputHandler = inputHandler;
    }

}
