package com.loicfrance.library.parsers;

import android.support.annotation.NonNull;

/**
 * Created by rfrance on 27/10/2015.
 */
public interface ParserObjectFiller<T> {
    @NonNull T getRoot();
    @NonNull T getObject(String tagName);
    void newAttrib(T obj, String attrName, String attrValue);
    void newAttrib(T obj, String attrName, int attrResId);
    void text(T obj, String text);
    void addChild(T parent, T child);
    void objectClose(T obj);
    boolean skip(String tagName);
}
