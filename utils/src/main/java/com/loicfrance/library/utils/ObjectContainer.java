package com.loicfrance.library.utils;

/**
 * Created by Loic France on 22/06/2015.
 * instantiate this class if you want to make a final object modifiable.
 */
@SuppressWarnings("WeakerAccess") //remove "access can be private" warning
public class ObjectContainer<T> {
    private T o;
    public ObjectContainer() {}
    public ObjectContainer(T obj) { this.o = obj; }
    public ObjectContainer set(T o) { this.o = o; return this; }
    public T get() { return o; }
}
