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

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Loic France on 05/06/2015.
 */
public abstract class CommunicationManager {
    private static final String LOGTAG = "NET_THREAD";
    private static final int MSG_DATA = 0;
    private Handler networkHandler;
    private Handler localHandler;
    private HandlerThread handlerThread;
    private boolean running = false;
    private CommunicationListener listener;

    private Handler.Callback inputCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_DATA:
                    CommunicationManager cm = CommunicationManager.this;
                    cm.listener.onData(cm, (byte[]) msg.obj);
                    break;
                default : break;
            }
            return false;
        }
    };
    private Handler.Callback outputCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_DATA:
                    onOutputData((byte[]) msg.obj);
                default : break;
            }
            return false;
        }
    };

    public CommunicationManager(String name, @NonNull CommunicationListener listener) {
        handlerThread = new HandlerThread(name);
        this.listener = listener;
    }
    public CommunicationManager(String name, int priority, @NonNull CommunicationListener listener) {
        handlerThread = new HandlerThread(name, priority);
        this.listener = listener;
    }

    public void start() throws IOException {
        this.running = true;
        listener.onCommStart(this);
        handlerThread.start();
        networkHandler = new Handler(handlerThread.getLooper(), outputCallback);
        localHandler = new Handler(inputCallback);
    }

    public Handler getNetworkThreadHandler() {
        return this.networkHandler;
    }
    public Handler getCallingThreadHandler() {
        return this.localHandler;
    }

    public void write(byte[] bytes) {
        networkHandler.obtainMessage(MSG_DATA, bytes).sendToTarget();
    }
    public void stop(boolean safeQuit) {
        if(safeQuit && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            handlerThread.quitSafely();
        } else handlerThread.quit();
        this.running = false;
        listener.onCommEnd(this);
    }
    public final void stop() {
        stop(true);
    }


    protected void onInputData(byte[] data) {
        localHandler.obtainMessage(MSG_DATA, data).sendToTarget();
    }
    protected boolean onLocalInputData(int what, Object obj) {
        switch(what) {
            case MSG_DATA:
                CommunicationManager cm = CommunicationManager.this;
                cm.listener.onData(cm, (byte[]) obj);
                return true;
            default : return false;
        }
    }
    protected abstract void onOutputData(byte[] bytes);

    public boolean isRunning() { return this.running; }


    public CommunicationListener getCommunicationListener() {
        return this.listener;
    }
}
