package fr.isen.m2.elecauto.parkingsensors;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by stephane on 08/02/16.
 */
public class SensorFragment extends Fragment {

    private TextView tvDistance;
    private BluetoothSocket bluetoothSocket;
    private AnimationSet animation;

    public static SensorFragment newInstance(BluetoothSocket mmSocket) {
        SensorFragment fragment = new SensorFragment();
        fragment.bluetoothSocket = mmSocket;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        // Retrieving the view element
        tvDistance = (TextView) view.findViewById(R.id.sensor_tv_distance);


        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        tvDistance.setAnimation(animation);

        readData();

        return view;
    }

    public void readData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Read the data
                RetrieveData retrieveData = new RetrieveData(bluetoothSocket);
                retrieveData.run();
            }
        }).start();
    }

    /**
     * Thread that can read the data received from a bluetooth device.
     */
    private class RetrieveData extends Thread {
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Start the animation.
                            animation.start();
                            tvDistance.setText(finalValue);
                        }
                    });

                } catch (IOException e) {
                    Log.d(MainActivity.TAG, "Exception and reading buffer");
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
