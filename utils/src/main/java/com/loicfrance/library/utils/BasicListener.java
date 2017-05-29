package com.loicfrance.library.utils;

/**
 * Created by Loic France on 26/09/2015.
 * a simple listener that can be used for simple callbacks, with a integer as an id.
 * Use it as you want.
 */
public interface BasicListener<T> {
    void onCall(int requestId, T value);
}
