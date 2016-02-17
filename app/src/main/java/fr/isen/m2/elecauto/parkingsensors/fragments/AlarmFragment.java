package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveData;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveDataAlarm;

/**
 * Created by stephane on 10/02/16.
 */
public class AlarmFragment extends SocketFragment implements ClosingFragment {

    public static final String TAG = AlarmFragment.class.getSimpleName();

    private ConfigurationFragment configurationFragment;
    private RetrieveData retrieveData;
    private LinearLayout llNotSafe;
    private LinearLayout llSafe;

    private boolean displayedAlarmNotSafe = false;

    private MediaPlayer mp;

    public static AlarmFragment newInstance(BluetoothSocket bluetoothSocket,
                                            ConfigurationFragment configurationFragment) {
        AlarmFragment fragment = new AlarmFragment();
        fragment.bluetoothSocket = bluetoothSocket;
        fragment.configurationFragment = configurationFragment;
        fragment.displayedAlarmNotSafe = false;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_alarm_safe, container, false);

        // Retrieving the view elements
        llNotSafe = (LinearLayout) view.findViewById(R.id.alarm_not_safe);
        llSafe = (LinearLayout) view.findViewById(R.id.alarm_safe);

        mp = MediaPlayer.create(getContext(), R.raw.warning_sound);

        view.findViewById(R.id.btn_call_police).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "tel:17";
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(callIntent);
            }
        });

        view.findViewById(R.id.btn_alarm_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying() && getActivity() != null) {
                    mp.stop();
                    mp.release();
                    mp = MediaPlayer.create(getActivity(), R.raw.warning_sound);
                }
            }
        });

        readData();

        return view;
    }


    public void readData() {
        final AlarmFragment alarmFragment = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Read the data
                retrieveData = new RetrieveDataAlarm(bluetoothSocket, getActivity(), alarmFragment);
                retrieveData.run();
            }
        }).start();
    }

    public void displayNotSafe() {
        llNotSafe.setVisibility(View.VISIBLE);
        llSafe.setVisibility(View.GONE);
        if (displayedAlarmNotSafe == false) {
            mp.start();
        }
        displayedAlarmNotSafe = true;
    }

    @Override
    public void close() {
        retrieveData.cancel();
        configurationFragment.init();
    }
}
