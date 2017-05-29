package com.loicfrance.library.hardware;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;


import com.loicfrance.library.utils.LogD;

import java.util.EventListener;

/**
 * Created by Loic France on 04/05/2015.
 */
public class OrientationSensor implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] calibration = new float[9];
    private float[] mOrientation = new float[3];

    private Context context;
    private int delay;
    private OnOrientationChangeListener listener;

    public interface OnOrientationChangeListener extends EventListener {
        void onOrientationChanged(float yaw, float pitch, float roll);
    }

    public OrientationSensor(Context context, int delay, OnOrientationChangeListener listener) {

        this.context = context;
        this.delay = delay;
        this.listener = listener;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }
    public void stopSensor() {
        mSensorManager.unregisterListener(this);
        LogD.d("ORIENTATION_SENSOR", "sensor unregistered");
    }
    public void startSensor() {
        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mSensorManager.registerListener(this, mAccelerometer, delay);
        mSensorManager.registerListener(this, mMagnetometer, delay);
        LogD.d("ORIENTATION_SENSOR", "sensor registered");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            //calibrate the rotation matrix
            Matrix.multiplyMM(mR, 0, mR, 0, calibration, 0);
            SensorManager.getOrientation(mR, mOrientation);
            listener.onOrientationChanged(mOrientation[0], mOrientation[1], mOrientation[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void calibrate() {
        Matrix.invertM(calibration, 0, mR, 0);
    }
}

