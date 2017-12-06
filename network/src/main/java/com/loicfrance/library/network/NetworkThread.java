package com.loicfrance.library.network;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Loic France on 05/06/2015.
 */
public abstract class NetworkThread extends Thread {
    private static final String LOGTAG = "NET_THREAD";
    public static final int REQUEST_ID_OFFSET_DATA = 0;
    public static final int REQUEST_ID_OFFSET_COMM_START = 1;
    public static final int REQUEST_ID_OFFSET_COMM_STOP = 2;
    public int requestId_DATA;
    public int requestId_COMM_START;
    public int requestId_COMM_STOP;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Handler inputHandler;
    private boolean running = false;
    private int inputMinSize = 1;


    private int inputMaxSize = 100;

    public NetworkThread(@NonNull Handler inputHandler, int requestId_offset) {
        this.inputHandler = inputHandler;
        this.requestId_DATA = requestId_offset + REQUEST_ID_OFFSET_DATA;
        this.requestId_COMM_START = requestId_offset + REQUEST_ID_OFFSET_COMM_START;
        this.requestId_COMM_STOP = requestId_offset + REQUEST_ID_OFFSET_COMM_STOP;
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

    protected void init(InputStream inputStream, OutputStream outputStream) {
        init(new DataInputStream(inputStream), new DataOutputStream(outputStream));
    }
    protected void init(DataInputStream input, DataOutputStream output) {
        in = input;
        out = output;
    }

    @Override
    public abstract void run();

    public boolean isRunning() { return this.running; }
    protected Handler getInputHandler() { return this.inputHandler; }

    protected void process() {
        running = true;
        //notify the handler that the communication has started
        Message msg = inputHandler.obtainMessage(requestId_COMM_START);
        inputHandler.sendMessage(msg);

        byte[] input;
        //listen continuously for incoming message, until the input data is null
        while (running) {
            if ((input = readData()) == null) {
                //LogD.i(LOGTAG, "no more data coming from the network.");
                running = false;
            } else {
                //LogD.i(LOGTAG, "data read (length= " + input.length + ").");
                if (input.length > 0 && inputHandler != null) {
                    msg.what = requestId_DATA;
                    msg.obj = input;
                    inputHandler.sendMessage(msg);
                }
            }
        }
        //LogD.i(LOGTAG, "network thread shutting down.");

        //notify the handler that the communication has stopped
        msg.what = requestId_COMM_STOP;
        msg.obj = null;
        inputHandler.sendMessage(msg);
    }

    public void close() {
        running = false;
    }


    private byte[] readData() {
        int len = 0;
        byte[] buffer = new byte[this.inputMaxSize];
        try {
            do {
                len += in.read(buffer, len, this.inputMaxSize-len);
                //LogD.i(LOGTAG, "read " + len +" bytes from input stream.");
                if (len < 0) {
                    //LogD.e(LOGTAG, "Message Length Error : length = " + len);
                    return null;
                }
            } while(len > 0 && len < this.inputMaxSize && in.available() > 0);
        } catch (Exception e) {
            //LogD.e(LOGTAG, "Error reading data : ");
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
            //LogD.e(LOGTAG, "Error sending message :");
            e.printStackTrace();
            return false;
        }
    }

    public void setInputHandler(@NonNull Handler inputHandler) {
        this.inputHandler = inputHandler;
    }

}
