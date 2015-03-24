package com.diana.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;

    BluetoothAdapter mBluetoothAdapter = MainActivity.mBluetoothAdapter;

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    ConnectedThread mConnectedThread;
    Handler mHandler;

    public ConnectThread(BluetoothDevice device, Handler handler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        mHandler = handler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, mHandler);
        mConnectedThread.start();

    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel(Context context) {
        try {
            mmSocket.close();
            Toast.makeText(context, "DISCONNECT", Toast.LENGTH_LONG).show();
        } catch (IOException e) { }
    }
}