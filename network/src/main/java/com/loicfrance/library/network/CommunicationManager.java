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


    protected void onInputData(byte[] data) {
        localHandler.obtainMessage(MSG_DATA, data).sendToTarget();
    }
    protected abstract void onOutputData(byte[] bytes);

    public boolean isRunning() { return this.running; }
    protected CommunicationListener getListener() {
        return this.listener;
    }
    public void setListener(@NonNull CommunicationListener listener) {
        this.listener = listener;
    }

}
