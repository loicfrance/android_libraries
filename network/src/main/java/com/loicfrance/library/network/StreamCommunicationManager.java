package com.loicfrance.library.network;

import android.support.annotation.NonNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Loic France on 08/12/2017.
 */

public class StreamCommunicationManager extends CommunicationManager {

    private enum READ {NO, ONCE, INFINITE}
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private int inputMaxSize = 1;
    private READ read = READ.NO;

    private Thread inputThread = new Thread(new Runnable() {
        @Override
        public void run() {
            byte[] input;
            while (isReading()) {
                input = readData();

                if(read == READ.ONCE) read = READ.NO;

                if (input == null) {
                    //LogD.i(LOGTAG, "no more data coming from the network.");
                    StreamCommunicationManager.this.stop(true);
                } else {
                    //LogD.i(LOGTAG, "data read (length= " + input.length + ").");
                    if (input.length > 0) {
                        onInputData(input);
                    }
                }
            }
        }
    });
    public void readOnce() {
        if(!isReading()) {
            inputThread.start();
        }
        read = READ.ONCE;
    }
    public void read() {
        if(!isReading()) {
            inputThread.start();
        }
        read = READ.INFINITE;
    }
    public void stopReading() {
        read = READ.NO;
    }
    public boolean isReading() {
        return this.read != READ.NO;
    }

    public StreamCommunicationManager(String name, @NonNull CommunicationListener listener) {
        super(name, listener);
    }
    protected void setInputStream(InputStream is) {
        setInputStream(new DataInputStream(is));
    }
    protected void setInputStream(DataInputStream is) {
        this.in = is;
    }
    protected void setOutputStream(OutputStream os) {
        setOutputStream(new DataOutputStream(os));
    }
    protected void setOutputStream(DataOutputStream os) {
        this.out = os;
    }
    protected void init(InputStream inputStream, OutputStream outputStream) {
        init(new DataInputStream(inputStream), new DataOutputStream(outputStream));
    }
    protected void init(DataInputStream input, DataOutputStream output) {
        in = input;
        out = output;
    }
    @Override
    public void stop(boolean safeQuit) {
        super.stop(safeQuit);
        inputThread.interrupt();
    }

    @Override
    protected void onOutputData(byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected byte[] readData() {
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
}
