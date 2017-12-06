package com.loicfrance.library.network;

import android.os.Handler;
import android.os.Message;


/**
 * Created by Loic France on 22/06/2015.
 */
public class NetworkHandler {

    private NetworkThread thread;
    private Handler inputHandler;
    private InputListener listener;
    private boolean continuousMode;
    private byte[] buffer;
    private Handler senderHandler;
    private final Handler.Callback hcb = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == thread.requestId_DATA)
                listener.onInput((byte[]) msg.obj);
            return true;
        }
    };
    private int continuousMode_delay;

    public NetworkHandler(boolean continuousMode) {
        this.continuousMode = continuousMode;
        this.continuousMode_delay = 20; // 20 ms <=> 50 Hz
    }
    public NetworkHandler() {
        this(false);
    }

    public void setNetworkThread(NetworkThread thread) {
        this.thread = thread;
    }

    public void start() {
        thread.start();
    }

    public void pause() {
        thread.interrupt();
    }

    public void stop() {
        thread.close();
    }

    public void setInputListener(final InputListener listener) {
        this.listener = listener;
        if (inputHandler == null) {
            inputHandler = new Handler(hcb);
            thread.setInputHandler(inputHandler);
        }
    }

    /**
     * Sets the output buffer to the given value.
     * If continuous mode is not enabled, the buffer will immediately be sent.
     * Otherwise, the buffer will be sent on the next scheduled time.
     * use {@link #enableContinuousMode(int)} or {@link #enableContinuousMode()} <!--
     * -->to enable continuous mode, {@link #disableContinuousMode()} to disable it, or <!--
     * -->{@link #isContinuousModeEnabled()} to know it it is enabled or not.
     * If  continuous mode is enabled, you can use
     * @param buffer the byte array to be sent
     */
    public void send(byte[] buffer) {
        this.buffer = buffer;
        if (!continuousMode) {
            thread.send(buffer);
        }
    }

    /**
     * continuously send message to the network.
     *
     * @param delay time, in millis, betwwen two call of the 'send' method of the network thread
     */
    public void enableContinuousMode(final int delay) {
        this.setContinuousModeDelay(delay);
        this.enableContinuousMode();
    }
    public void enableContinuousMode() {
        if (senderHandler == null) senderHandler = new Handler();
        senderHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (continuousMode) {
                    if (buffer != null)
                        thread.send(buffer);
                    senderHandler.postDelayed(this, continuousMode_delay);
                }

            }
        }, continuousMode_delay);
    }
    public void setContinuousModeDelay(int ms) {
        this.continuousMode_delay = ms;
    }

    public void disableContinuousMode() {
        continuousMode = false;
    }

    public boolean isContinuousModeEnabled() {
        return continuousMode;
    }



}
