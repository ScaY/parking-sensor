package fr.isen.m2.elecauto.parkingsensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by stephane on 08/02/16.
 */
public class ConfigurationFragment extends Fragment {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID UUID_APP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket bluetoothSocket;

    private TextView tvStatusBluetooh;
    private TextView tvPaired;
    private TextView tvConnected;
    private ImageView imgBluetooth;
    private ImageView imgPaired;
    private ImageView imgConnected;
    private ImageButton btnRefresh;
    private ImageButton btnStart;
    private GridLayout glLoading;
    private com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar dotsProgressBar;

    public static ConfigurationFragment newInstance() {
        return new ConfigurationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieving the view element
        tvStatusBluetooh = (TextView) view.findViewById(R.id.home_tv_bluetooh);
        tvPaired = (TextView) view.findViewById(R.id.home_tv_paired);
        tvConnected = (TextView) view.findViewById(R.id.home_tv_connected);
        imgBluetooth = (ImageView) view.findViewById(R.id.home_img_bluetooth);
        imgConnected = (ImageView) view.findViewById(R.id.home_img_connected);
        imgPaired = (ImageView) view.findViewById(R.id.home_img_paired);
        btnRefresh = (ImageButton) view.findViewById(R.id.home_btn_refresh);
        btnStart = (ImageButton) view.findViewById(R.id.home_btn_start);
        glLoading = (GridLayout) view.findViewById(R.id.loading);
        dotsProgressBar = (DilatingDotsProgressBar) view.findViewById(R.id.progress);

        // Initialize the configuration state image
        setImageView(imgBluetooth, R.mipmap.invalid);
        setImageView(imgPaired, R.mipmap.invalid);
        setImageView(imgConnected, R.mipmap.invalid);

        // Retrieving the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Refresh the configuration.
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoading();
                boolean bluetooth = checkBluetooth();
                if (bluetooth) {
                    boolean retrievedPairedDevice = retrievePairedDevice();
                    if (retrievedPairedDevice) {
                        connectDevice();
                    }
                }
            }
        });

        // Start to retrieve the distance from the sensor
        // and replace the fragment to display the distance.
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, SensorFragment.newInstance(bluetoothSocket));
                ft.commit();
            }
        });

        // Display the refresh button
        displayRefreshButton();

        return view;
    }

    /**
     * Display the start button, allowing to view the distance from the bluetooth device.
     */
    private void displayStartButton() {
        btnRefresh.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
        glLoading.setVisibility(View.GONE);
    }

    /**
     * Display the refresh button, allowing to refresh the configuration.
     */
    private void displayRefreshButton() {
        btnRefresh.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
        glLoading.setVisibility(View.GONE);
    }

    private void displayLoading() {
        btnRefresh.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
        glLoading.setVisibility(View.VISIBLE);
        dotsProgressBar.showNow();

    }

    /**
     * Check if the bluetooth is activated and update the UI
     *
     * @return true if the bluetooth is activated, false otherwise
     */
    private boolean checkBluetooth() {
        boolean result = false;
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.d(MainActivity.TAG, "Bluetooth not supported");
            tvStatusBluetooh.setText("Bluetooth not supported");
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                setImageView(imgBluetooth, R.mipmap.invalid);
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.d(MainActivity.TAG, "Bluetooth activated");
                setImageView(imgBluetooth, R.mipmap.valid);
                result = true;
            }
        }
        return result;
    }

    /**
     * Retrieve the paired device and update the UI
     *
     * @return
     */
    private boolean retrievePairedDevice() {
        boolean result = false;
        // Retrieving the paired device
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            device = pairedDevices.iterator().next();
            Log.d(MainActivity.TAG, "Paired with " + device.getName() + " " + device.getName() + " " + device.getUuids()[0].getUuid());
            tvPaired.setText("Paired with " + device.getName());
            setImageView(imgPaired, R.mipmap.valid);
            result = true;
        } else {
            setImageView(imgPaired, R.mipmap.invalid);
            Log.d(MainActivity.TAG, "Not paired");
        }

        return result;
    }

    /**
     * Create a connection with the connected device
     */
    public void connectDevice() {
        ConnectDevice connectDevice = new ConnectDevice(device);
        connectDevice.execute();
    }

    private void setImageView(ImageView imageView, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getResources().getDrawable(resource, getActivity().getTheme()));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(resource));
        }
    }

    /**
     * Thread that can create a socket with a bluetooth device
     */
    private class ConnectDevice extends AsyncTask<Void, Void, Boolean> {

        public ConnectDevice(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                Log.d(MainActivity.TAG, "Try to create socket client");
                tmp = device.createRfcommSocketToServiceRecord(UUID_APP);
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "Creating client socket failed " + e.getMessage());
            }
            Log.d(MainActivity.TAG, "Client socket created.");
            bluetoothSocket = tmp;
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.d(MainActivity.TAG, "Trying to connect with the socket...");
                bluetoothSocket.connect();
                Log.d(MainActivity.TAG, "Socket connected " + bluetoothSocket.getRemoteDevice().getName() + ".");
                Log.d(MainActivity.TAG, "Socket connected " + bluetoothSocket.getRemoteDevice().getUuids()[0].getUuid());
                result = true;
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    Log.d(MainActivity.TAG, "Socket connection failed");
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.d(MainActivity.TAG, "Closing socket fail " + closeException.getMessage());
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(Boolean connected) {
            if (connected) {
                setImageView(imgConnected, R.mipmap.valid);
                tvConnected.setText("Connected with " + bluetoothSocket.getRemoteDevice().getName());
                displayStartButton();
            } else {
                displayRefreshButton();
            }
        }

    }
}
