package fr.isen.m2.elecauto.parkingsensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Home extends ActionBarActivity {

    private final static int REQUEST_ENABLE_BT = 1;

    // SPP UUID service - this should work for most devices
    private static final UUID UUID_APP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "HOME_TAG";

    private BluetoothDevice device;
    private BluetoothAdapter mBluetoothAdapter;

    private Home ref = this;

    private TextView tvStatusBluetooh;
    private TextView tvPaired;
    private TextView tvConnected;
    private TextView tvData;
    private ImageView imgBluetooth;
    private ImageView imgPaired;
    private ImageView imgConnected;
    private ImageButton btnRefresh;
    private ImageButton btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieving the several view element
        tvStatusBluetooh = (TextView) findViewById(R.id.home_tv_bluetooh);
        tvPaired = (TextView) findViewById(R.id.home_tv_paired);
        tvConnected = (TextView) findViewById(R.id.home_tv_connected);
        imgBluetooth = (ImageView) findViewById(R.id.home_img_bluetooth);
        imgConnected = (ImageView) findViewById(R.id.home_img_connected);
        imgPaired = (ImageView) findViewById(R.id.home_img_paired);
        btnRefresh = (ImageButton) findViewById(R.id.home_btn_refresh);
        btnStart = (ImageButton) findViewById(R.id.home_btn_start);
        tvData = (TextView) findViewById(R.id.homeData);

        imgBluetooth.setImageDrawable(getResources().getDrawable(R.mipmap.invalid));
        imgPaired.setImageDrawable(getResources().getDrawable(R.mipmap.invalid));
        imgConnected.setImageDrawable(getResources().getDrawable(R.mipmap.invalid));

        // Retrieving the bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Refresh the configuration
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDevice(retrievePairedDevice(checkBluetooth()));
            }
        });

        // Start to retrieve the distance from the sensor
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ref, Sensor.class);
                startActivity(intent);
            }
        });

        btnRefresh.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
    }

    /**
     * Check if the bluetooth is activated
     * @return true if the bluetooth is activated, false otherwise
     */
    private boolean checkBluetooth() {
        boolean result = false;
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(TAG, "Bluetooth not supported");
            tvStatusBluetooh.setText("Bluetooth not supported");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                imgBluetooth.setImageDrawable(getResources().getDrawable(R.mipmap.invalid));
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.d(TAG, "Bluetooth activated");
                imgBluetooth.setImageDrawable(getResources().getDrawable(R.mipmap.valid));
                result = true;
            }
        }
        return result;
    }

    /**
     * Retrieve the paired device
     * @param bluetooth
     * @return
     */
    private boolean retrievePairedDevice(boolean bluetooth) {
        boolean result = false;
        if (bluetooth) {
            // Retrieving the paired device
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                device = pairedDevices.iterator().next();
                Log.d(TAG, "Paired with " + device.getName() + " " + device.getName() + " " + device.getUuids()[0].getUuid());
                tvPaired.setText("Paired with " + device.getName());
                imgPaired.setImageDrawable(getResources().getDrawable(R.mipmap.valid));
                result = true;
            } else {
                Log.d(TAG, "Not paired");
            }
        }
        return result;
    }

    /**
     * Create a connection with the connected device
     * @param paired
     */
    public void connectDevice(boolean paired) {
        if(paired){
            ClientSocket clientSocket = new ClientSocket(device);
            clientSocket.run();
        }
    }

    /**
     * Thread that can create a socket with a bluetooth device
     */
    private class ClientSocket extends Thread {

        private final BluetoothSocket mmSocket;

        public ClientSocket(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                Log.d(TAG, "Try to create socket client");
                tmp = device.createRfcommSocketToServiceRecord(UUID_APP);
            } catch (IOException e) {
                Log.d(TAG, "Creating client socket failed " + e.getMessage());
            }
            Log.d(TAG, "Client socket created.");
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.d(TAG, "Trying to connect with the socket...");
                mmSocket.connect();
                Log.d(TAG, "Socket connect with " + mmSocket.getRemoteDevice().getName() + ".");
                imgConnected.setImageDrawable(getResources().getDrawable(R.mipmap.valid));
                tvConnected.setText("Connected with " + mmSocket.getRemoteDevice().getName());
                // Read the data
                ConnectedThread connectedThread = new ConnectedThread(mmSocket);
                connectedThread.run();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    Log.d(TAG, "Socket connection failed");
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d(TAG, "Closing socket fail " + closeException.getMessage());
                }
                return;
            }

            Log.d(TAG, "Socket connected  " + mmSocket.getRemoteDevice().getUuids()[0].getUuid());
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * Thread that can read the data received from a bluetooth device.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                Log.d(TAG, "Retrieving input and output stream...");
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "Retrieving input and output failed.");
            }

            Log.d(TAG, "Input and ouput stream retrieved.");
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    Log.d(TAG, "Trying to read inputstream");
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.d(TAG, "Data received");
                    for (int i = 0; i < bytes; i++) {
                        Log.d(TAG, String.valueOf(buffer[i]));
                        tvData.setText(String.valueOf(buffer[i]));
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Exception and reading buffer");
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




