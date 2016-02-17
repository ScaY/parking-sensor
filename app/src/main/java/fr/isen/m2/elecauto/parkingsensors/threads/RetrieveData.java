package fr.isen.m2.elecauto.parkingsensors.threads;

/**
 * Created by stephane on 14/02/16.
 */

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;

/**
 * Thread that can read the data received from a bluetooth device.
 */
public abstract class RetrieveData extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public RetrieveData(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            Log.d(MainActivity.TAG, "Retrieving input and output stream...");
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, "Retrieving input and output failed.");
        }

        Log.d(MainActivity.TAG, "Input and ouput stream retrieved.");
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                Log.d(MainActivity.TAG, "Trying to read inputstream");
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String tmpValue = "";
                Log.d(MainActivity.TAG, "Data received");
                for (int i = 0; i < bytes; i++) {
                    int value = Integer.valueOf(buffer[i]);
                    char valueChar = (char) value;
                    tmpValue += String.valueOf(valueChar);
                    Log.d(MainActivity.TAG, "Byte at " + i + " -> " + String.valueOf(valueChar));
                }
                Log.d(MainActivity.TAG, "finalValue " + tmpValue);
                final String finalValue = tmpValue.replaceAll("\\s+", "");
                postAction(finalValue);
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "Exception and reading buffer");
                break;
            }
        }
    }


    public void cancel() {
        try {
            Log.d(MainActivity.TAG, "Trying to close the Socket");
            mmSocket.close();
            mmInStream.close();
            mmOutStream.close();
            Log.d(MainActivity.TAG, "Socket closed");
        } catch (IOException e) {
            Log.d(MainActivity.TAG, "Exception when trying to close the socket");
        }
    }

    public abstract void postAction(String value);
}
