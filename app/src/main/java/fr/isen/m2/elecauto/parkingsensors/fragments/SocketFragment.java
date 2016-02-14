package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;

/**
 * Created by stephane on 14/02/16.
 */
public class SocketFragment extends Fragment {

    protected BluetoothSocket bluetoothSocket;

    public static SocketFragment init(BluetoothSocket bluetoothSocket){
        SocketFragment fragment = new SocketFragment();
        fragment.bluetoothSocket = bluetoothSocket;
        return fragment;
    }

}
