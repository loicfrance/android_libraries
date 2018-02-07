package com.loicfrance.library.network.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loicfrance.library.utils.BasicListener;
import com.loicfrance.library.utils.LogD;
import com.loicfrance.library.utils.ObjectContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Loic France on 22/06/2015.
 */
@SuppressWarnings("WeakerAccess") //remove "access can be private" warning
public abstract class BluetoothManager {
    public static final int STATE_ON = BluetoothAdapter.STATE_ON;
    public static final int STATE_OFF = BluetoothAdapter.STATE_OFF;
    public static final int STATE_TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
    public static final int STATE_TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;


    private static BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    private static BroadcastReceiver deviceDiscoveryReceiver;
    private static BluetoothAdapter.LeScanCallback deviceDiscoveryLeCallback;
    private static boolean isDeviceDiscovering = false;


    public static BluetoothAdapter getAdapter() {
        return bluetooth;
    }
//__________________________________________________________________________________________________bluetooth state
//--------------------------------------------------------------------------------------------------3

    /**
     * @param ctx application context
     * @return {@code true} if the device has a Bluetooth adapter
     */
    public static boolean isAvailable(Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }
    /**
     * @param ctx application context
     * @return {@code true} if the device has a Bluetooth Low Energy (BLE) adapter
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isBLEAvailable(Context ctx) {
        return ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * @return {@code true} if the bluetooth of the device is currently activated, {@code false}
     *          otherwise
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public static boolean isEnabled() {
        return bluetooth.isEnabled();
    }


    /**
     * Activates the bluetooth of the device
     * @param context application context used to launch the intent
     * @param ask if {@code true}, the user will be asked permission. Otherwise,
     *              the bluetooth will be activated without him being notified
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void enable(Activity context, boolean ask, int requestId) {
        if (ask) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(enableBtIntent, requestId);
        } else {
            bluetooth.enable();
        }
    }

    /**
     * Disables the bluetooth of the device
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void disable() {
        bluetooth.disable();
    }

//__________________________________________________________________________________________________device name & address
//--------------------------------------------------------------------------------------------------3

    /**
     * @return the name the device. This is the name other devices will see if your device is
     * discoverable
     */
    public static String getName() {
        return bluetooth.getName();
    }

    /**
     * modifies the name of the device. This is the name other devices will see if your device is
     * discoverable
     * @param name the new name of th device
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void setName(String name) {
        bluetooth.setName(name);
    }

    /**
     * @return the address of the device.
     */
    public static String getAddress() {
        return bluetooth.getAddress();
    }


//__________________________________________________________________________________________________device discoverability
//--------------------------------------------------------------------------------------------------1

    /**
     * allow other device to see this device for the given duration.
     * @param time duration, <b>in seconds</b>, of the device discoverability.
     */
    public static void enableDiscoverability(Activity context, int time) {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
        context.startActivity(discoverableIntent);
    }
//__________________________________________________________________________________________________device discovery
//--------------------------------------------------------------------------------------------------1


    /**
     * Starts discovering the surrounding devices. If you already started a discovering,
     * this function will do nothing.
     * <br/>
     * When a device is discovered, the listener's
     * {@link DeviceDiscoveryListener#deviceDiscovered(BluetoothDevice)} function is called
     * with the discovered device as the parameter.
     * <br/>
     * When the timeout period has been reached, the discovery is cancelled and the listener's
     * {@link DeviceDiscoveryListener#discoveryFinished()} function is called.
     * <br/>
     * You can also stop the discovery by calling the{@link #cancelDiscovery(Context)} method.
     * Be aware that doing so, the listener's {@link DeviceDiscoveryListener#discoveryFinished()}
     * method will not be called.
     * @param context the context from which the discovery is done
     * @param listener will be called when a new device is discovered,
     *                 or when the timeout is reached
     * @param time timeout, in milliseconds. the discovery is cancelled when this time has passed
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void startDiscovery(final Context context,
                                      final DeviceDiscoveryListener listener, long time) {
        if (isDeviceDiscovering) {
            return; // Don't do several discoveries in the same time.
        }
        Handler receiverCleaner = new Handler();


        deviceDiscoveryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    listener.deviceDiscovered((BluetoothDevice)
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(deviceDiscoveryReceiver, filter);
        if (!bluetooth.startDiscovery()) listener.discoveryFinished();
        isDeviceDiscovering = true;


        // Unregister the BroadcastReceiver at the end of the time.
        receiverCleaner.postDelayed(new Runnable() {
            @Override
            @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
            public void run() {
                if(isDeviceDiscovering) {
                    cancelDiscovery(context);
                    listener.discoveryFinished();
                }
            }
        }, time);
    }
    /**
     * Starts discovering the surrounding devices. If you already started a discovering,
     * this function will do nothing.
     * <br/>
     * When a device is discovered, the listener's
     * {@link DeviceDiscoveryListener#deviceDiscovered(BluetoothDevice)} function is called
     * with the discovered device as the parameter.
     * <br/>
     * When the timeout period has been reached, the discovery is cancelled and the listener's
     * {@link DeviceDiscoveryListener#discoveryFinished()} function is called.
     * <br/>
     * You can also stop the discovery by calling the{@link #cancelLeDiscovery()} method.
     * Be aware that doing so, the listener's {@link DeviceDiscoveryListener#discoveryFinished()}
     * method will not be called.
     * @param context the context from which the discovery is done
     * @param listener will be called when a new device is discovered,
     *                 or when the timeout is reached
     * @param time timeout, in milliseconds. the dicovery is cancelled when thi time has passed
     * @param uuidFilter the uuid of specific types of peripherals if you want to filter them.
     *                  {@code null} for no filter.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void startLeDiscovery(final Activity context, final DeviceDiscoveryListener listener,
                                        long time, @Nullable UUID[] uuidFilter) {
        if (isDeviceDiscovering) {
            return; // Don't do several discoveries in the same time.
        }
        final Handler h = new Handler();
        deviceDiscoveryLeCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice bluetoothDevice,
                                 int rssi, byte[] scanRecord) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.deviceDiscovered(bluetoothDevice);
                    }
                });
            }
        };
        if(uuidFilter == null || uuidFilter.length == 0)
            bluetooth.startLeScan(deviceDiscoveryLeCallback);
        else
            bluetooth.startLeScan(uuidFilter, deviceDiscoveryLeCallback);

        isDeviceDiscovering = true;


        // Unregister the BroadcastReceiver at the end of the time.
        Handler receiverCleaner = new Handler();
        receiverCleaner.postDelayed(new Runnable() {
            @Override
            @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
            public void run() {
                if(isDeviceDiscovering) {
                    cancelLeDiscovery();
                    listener.discoveryFinished();
                }
            }
        }, time);
    }

    /**
     * Cancels the device discovery. Do not forget to call this function when you don't
     * need the discovery to be running, because device discovering costs a lot of energy
     * @param context
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void cancelDiscovery(Context context) {
        bluetooth.cancelDiscovery();
        if(isDeviceDiscovering && deviceDiscoveryReceiver != null) {
            context.unregisterReceiver(deviceDiscoveryReceiver);
            deviceDiscoveryReceiver = null;
            isDeviceDiscovering = false;
        }
    }
    /**
     * Cancels the device discovery. Do not forget to call this function when you don't
     * need the discovery to be running, because device discovering costs a lot of energy
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public static void cancelLeDiscovery() {
        if(deviceDiscoveryLeCallback != null) {
            bluetooth.stopLeScan(deviceDiscoveryLeCallback);
            deviceDiscoveryLeCallback = null;
            isDeviceDiscovering = false;
        }
    }

//__________________________________________________________________________________________________choose device
//--------------------------------------------------------------------------------------------------1

    /**
     * This function activates bluetooth (requesting user permission), looks for surrounding devices,
     * show all of them in a dialog window and asks the user to pick one. When the user has
     * picked one or cancelled the dialog, the listener is called with the selected device, or null.
     * @param context
     * @param title dialog box title
     * @param listener will be called when the user has made his choice.
     * @param requestTag tag that will be used when calling the listener
     * @param enableRequestId id that will be used to request bluetooth enable if not previously enabled
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public static void chooseDevice(final Activity context, @Nullable String title,
                                    final BasicListener<BluetoothDevice> listener,
                                    final int requestTag, int enableRequestId) {

        if (!isEnabled()) enable(context, true, enableRequestId);

        final ObjectContainer<Integer> tmp = new ObjectContainer<>(0);

        //- - - - - - - - - - - - - - - - - - - - setting up the view.

        final Dialog dialog = new Dialog(context);
        LinearLayout dialogLayout = new LinearLayout(context);
        LinearLayout btnsLayout = new LinearLayout(context);
        final ListView devicesLV = new ListView(context);
        final Button acceptBtn = new Button(context);
        Button cancelBtn = new Button(context);
        BluetoothDevice selected;

        BluetoothDevice choice;
        final List<BluetoothDevice> devices = new ArrayList<>();
        final List<HashMap<String, String>> devicesHmaps = new ArrayList<>();
        final HashMap<String, String> deviceHmap = new HashMap<>();

        if (title != null) {
            dialog.setTitle(title);
        } else {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        btnsLayout.setOrientation(LinearLayout.HORIZONTAL);
        acceptBtn.setText(context.getString(android.R.string.ok));
        cancelBtn.setText(context.getString(android.R.string.cancel));

        acceptBtn.setEnabled(false); // only enable when a device is selected.


        btnsLayout.setGravity(Gravity.CENTER);
        btnsLayout.addView(cancelBtn);
        btnsLayout.addView(acceptBtn);
        dialogLayout.addView(devicesLV);
        dialogLayout.addView(btnsLayout);
        dialog.setContentView(dialogLayout);
        dialog.show();

        //- - - - - - - - - - - - - - - - - - - - setting up buttons' effects.

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
            public void onClick(View v) {
                listener.onCall(requestTag, null);
                bluetooth.cancelDiscovery();
                dialog.dismiss();
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
            public void onClick(View v) {
                LogD.d("BT_MANAGER", "device selection accepted");
                listener.onCall(requestTag, devices.get(tmp.get()));
                bluetooth.cancelDiscovery();
                dialog.dismiss();
            }
        });

        //- - - - - - - - - - - - - - - - - - - - setting up the ListView

        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogD.d("BT_MANAGER", "selected device : " +
                        devices.get(position).getName() + " : " + devices.get(position).getAddress());
                devicesLV.setSelection(position);
                devicesLV.setSelected(true);
                tmp.set(position);
                if (!acceptBtn.isEnabled()) acceptBtn.setEnabled(true);
            }
        });

        //- - - - - - - - - - - - - - - - - - - - starting device discovery.
        startDiscovery(context, new DeviceDiscoveryListener() {
            @Override
            public void deviceDiscovered(BluetoothDevice device) {
                LogD.d("BT_MANAGER", "found new device : " +
                        device.getName() + " : " + device.getAddress());
                deviceHmap.put("name", device.getName());
                deviceHmap.put("address", device.getAddress());
                devices.add(device);
                devicesHmaps.add(deviceHmap);
                ListAdapter adapter = new SimpleAdapter(context, devicesHmaps,
                        android.R.layout.simple_list_item_2,
                        new String[]{"name", "address"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                devicesLV.setAdapter(adapter);
                devicesLV.invalidate();
            }

            @Override
            public void discoveryFinished() {

            }
        }, 12000);

    }


//__________________________________________________________________________________________________ cleanUp
//--------------------------------------------------------------------------------------------------1

    /**
     * this method must be called on application's onDestroy to ensure that
     * the device is not trying to discover other devices because this
     * operation takes lots of energy.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    public void cleanUp(Activity context) {
        cancelDiscovery(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            cancelLeDiscovery();
        }
    }


}
