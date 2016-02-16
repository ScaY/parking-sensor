package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;

/**
 * Created by stephane on 14/02/16.
 */
public abstract class SocketFragment extends Fragment {

    protected BluetoothSocket bluetoothSocket;

    protected abstract void readData();
}
