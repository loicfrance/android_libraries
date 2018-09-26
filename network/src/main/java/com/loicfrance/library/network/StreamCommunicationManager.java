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

package com.loicfrance.library.network;

import android.support.annotation.IntRange;
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
        int len = 0, l;
        byte[] buffer = new byte[this.inputMaxSize];
        try {
            do {
                l = in.read(buffer, len, this.inputMaxSize-len);
                //LogD.i(LOGTAG, "read " + len +" bytes from input stream.");
                len += l;
            } while(l > 0 && 0 < len && len < this.inputMaxSize && in.available() > 0);
        } catch (Exception e) {
            //LogD.e(LOGTAG, "Error reading data : ");
            e.printStackTrace();
            return null;
        }
        return buffer;
    }

    public void setMaxInputSize(@IntRange(from=1)int max) {
        this.inputMaxSize = max;
    }
}
