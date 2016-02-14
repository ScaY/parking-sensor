package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveData;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveDataAlarm;

/**
 * Created by stephane on 10/02/16.
 */
public class AlarmFragment extends Fragment {

    private BluetoothSocket bluetoothSocket;

    public static AlarmFragment newInstance(BluetoothSocket bluetoothSocket) {
        AlarmFragment fragment = new AlarmFragment();
        fragment.bluetoothSocket = bluetoothSocket;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_alarm_safe, container, false);

        readData();

        return view;
    }


    public void readData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Read the data
                RetrieveData retrieveData = new RetrieveDataAlarm(bluetoothSocket, getActivity());
                retrieveData.run();
            }
        }).start();
    }


}
