package com.loicfrance.library.network;

/**
 * Created by Loic France on 09/12/2017.
 */

public interface CommunicationListener {
    void onData(CommunicationManager cm, byte[] data);
    void onCommStart(CommunicationManager cm);
    void onCommEnd(CommunicationManager cm);
}


