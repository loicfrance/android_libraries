package com.loicfrance.library.network;

import java.nio.ByteBuffer;


/**
 * Created by Loic France on 26/04/2015.
 */
public class ByteArrayConverter {

    /**
     * Returns the integer at the start index of the byte array.
     * @param arr byte array containing the four bytes of the integer
     * @param start of the first byte of the integer. must not exceed {@code arr.length - 4}
     * @return the integer at the start index
     */
    public static int byteArrayToInt(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 4).getInt();
    }

    /**
     * Returns the integer starting at index 0 of the byte array.
     * @param arr byte array containing the four bytes of the integer
     * @return the integer at the beginning of the array
     */
    public static int byteArrayToInt(byte[] arr) { // size 4
        return ByteBuffer.wrap(arr, 0, 4).getInt();
    }

    /**
     * Converts an integer to a 4-bytes array
     * @param a integer to be converted
     * @return a 4-bytes array containing the integer
     */
    public static byte[] intToByteArray(int a) { // size 4
        return ByteBuffer.allocate(4).putInt(a).array();
    }

    /**
     * Returns the short integer at the start index of the byte array.
     * @param arr byte array containing the two bytes of the integer
     * @param start of the first index of the integer. must not exceed {@code arr.length - 2}
     * @return the short integer at the start index
     */
    public static short byteArrayToShort(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 2).getShort();
    }

    /**
     * Returns the short integer starting at index 0 of the byte array.
     * @param arr byte array containing the two bytes of the integer
     * @return the short integer at the beginning of the array
     */
    public static short byteArrayToShort(byte[] arr) { //size 2
        return ByteBuffer.wrap(arr, 0, 2).getShort();
    }

    /**
     * Converts a short integer to a 2-bytes array
     * @param a the short integer to be converted
     * @return a 2-bytes array containing the short integer
     */
    public static byte[] shortToByteArray(short a) { // size 2
        return ByteBuffer.allocate(2).putShort(a).array();
    }

    /**
     * Returns the long integer at the start index of the byte array.
     * @param arr byte array containing the 8 bytes of the integer
     * @param start of the first index of the integer. must not exceed {@code arr.length - 8}
     * @return the long integer at the start index
     */
    public static long byteArrayToLong(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 8).getLong();
    }

    /**
     * Returns the long integer starting at index 0 of the byte array.
     * @param arr byte array containing the eight bytes of the integer
     * @return the long integer at the beginning of the array
     */
    public static long byteArrayToLong(byte[] arr) {
        return ByteBuffer.wrap(arr, 0, 8).getLong();
    }

    /**
     * Converts a long integer to an 8-bytes array
     * @param a the long integer to be converted
     * @return a 8-bytes array containing the short integer
     */
    public static byte[] longToByteArray(long a) {
        return ByteBuffer.allocate(8).putLong(a).array();
    }

    /**
     * Returns the float at the start index of the byte array.
     * @param arr byte array containing the 4 bytes of the float
     * @param start of the first index of the float. must not exceed {@code arr.length - 4}
     * @return the long integer at the start index
     */
    public static float byteArrayToFloat(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 4).getFloat();
    }

    /**
     * Returns the float starting at index 0 of the byte array.
     * @param arr byte array containing the four bytes of the float
     * @return the float at the beginning of the array
     */
    public static float byteArrayToFloat(byte[] arr) { // size 4;
        return ByteBuffer.wrap(arr, 0, 4).getFloat();
    }

    /**
     * Converts a float to a 4-bytes array
     * @param a the float to be converted
     * @return a 4-bytes array containing the float
     */
    public static byte[] floatToByteArray(float a) { // size 4
        return ByteBuffer.allocate(4).putFloat(a).array();
    }

    /**
     * Returns the double at the start index of the byte array.
     * @param arr byte array containing the 8 bytes of the double
     * @param start of the first index of the double. must not exceed {@code arr.length - 8}
     * @return the double at the start index
     */
    public static double byteArrayToDouble(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 8).getDouble();
    }

    /**
     * Returns the double starting at index 0 of the byte array.
     * @param arr byte array containing the eight bytes of the double
     * @return the double at the beginning of the array
     */
    public static double byteArrayToDouble(byte[] arr) { // size 4;
        return ByteBuffer.wrap(arr, 0, 8).getDouble();
    }

    /**
     * Converts a double to an 8-bytes array
     * @param a the double to be converted
     * @return a 8-bytes array containing the double
     */
    public static byte[] doubleToByteArray(double a) { // size 4
        return ByteBuffer.allocate(8).putDouble(a).array();
    }

    /**
     * Converts a (n*4+start)-bytes array to a float array of size n
     * @param n number of floats in the array (= size of array result)
     * @param arr byte array to extract the float array
     * @param start index of the first float in the byte array
     * @return the array of floats extracted
     */
    public static float[] byteArrayToFloatArray(byte[] arr, int n, int start) {
        float[] result = new float[n];
        ByteBuffer buff = ByteBuffer.wrap(arr);
        for (int i = 0; i < n; i++) {
            result[i] = buff.getFloat();
        }
        return result;
    }

    /**
     * Converts a (n*4)-bytes array to a float array of size n
     * @param n number of floats in the array (= size of array result)
     * @param arr byte array to extract the float array
     * @return the array of floats extracted
     */
    public static float[] byteArrayToFloatArray(byte[] arr, int n) {
        float[] res = new float[n];
        for (int i = 0; i < n; i++) {
            byte[] tmp = new byte[4];
            System.arraycopy(arr, 4 * i, tmp, 0, 4);
            //TODO optimize
            res[i] = byteArrayToFloat(tmp);
        }
        return res;
    }

    /**
     * Converts a float array to a bytes array
     * @param a float array to be converted to a byte array
     * @return a byte array containing all floats
     */
    public static byte[] floatArrayToByteArray(float[] a) {
        int n = a.length;
        byte[] res = new byte[n * 4];
        for (int i = 0; i < n; i++) {
            byte[] tmp = floatToByteArray(a[i]);
            System.arraycopy(tmp, 0, res, 4 * i, 4);
        }
        return res;
    }

    //8 booleans per byte.

    /**
     * Converts a boolean array to a byte array. each byte contains 8 booleans.
     * This means that the size of the byte array is {@code n/8}, where n is the number of booleans
     * @param a the boolean array to be converted
     * @return a byte array containing all booleans
     */
    public static byte[] booleanArrayToByteArray(boolean[] a) {
        byte[] result = new byte[(int) (a.length / 8 + 0.9f)];
        for (int i = 0; i < result.length; i++) {
            byte b = 0;
            short factor = 1;
            for (int j = i * 8; j < a.length && j < (i + 1) * 8; j++) {
                if (a[j]) {
                    b += factor;
                }
                factor *= 2;
            }
        }
        return result;
    }

    /**
     * Converts a byte array to a boolean array.
     * As each byte contains 8 bits, the size of the extracted boolean array is a multiple of 8
     * @param a the array of bytes to be converted to booleans
     * @return the array of booleans extracted from the byte array
     */
    public static boolean[] byteArrayToBooleanArray(byte[] a) {
        boolean[] res = new boolean[a.length * 8];
        for (int i = 0; i < a.length; i++) {
            byte b = a[i];
            for (int k = i * 8, j = 0; j < 8; j++) {
                res[k + j] = (b % 2 == 1);
                b /= 2;
            }
        }
        return res;
    }

    /**
     * Returns the char at the start index of the byte array.
     * @param arr byte array containing the 2 bytes of the char
     * @param start of the first index of the char. must not exceed {@code arr.length - 2}
     * @return the char at the start index
     */
    public static char byteArrayToChar(byte[] arr, int start) {
        return ByteBuffer.wrap(arr, start, 2).getChar();
    }

    /**
     * Returns the char starting at index 0 of the byte array.
     * @param arr byte array containing the two bytes of the char
     * @return the char at the beginning of the array
     */
    public static char byteArrayToChar(byte[] arr) {
        return ByteBuffer.wrap(arr, 0, 2).getChar();
    }

    /**
     * Converts a char to a 2-bytes array
     * @param a the char to be converted
     * @return a 2-bytes array containing the char
     */
    public static byte[] charToByteArray(char a) {
        return ByteBuffer.allocate(2).putChar(a).array();
    }

}