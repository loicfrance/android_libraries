package com.loicfrance.library.utils;

/**
 * Created by Loic France on 22/06/2015.
 * instantiate this class if you want to make a final object modifiable.
 */

public class ModifObject<T> {
    private T o;
    public ModifObject() {}
    public ModifObject(T obj) { this.o = obj; }
    public ModifObject set(T o) { this.o = o; return this; }
    public T get() { return o; }
}
