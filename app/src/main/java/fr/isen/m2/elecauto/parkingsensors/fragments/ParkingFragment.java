package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveData;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveDataParking;

/**
 * Created by stephane on 08/02/16.
 */
public class ParkingFragment extends SocketFragment implements ClosingFragment{

    public static final String TAG = ParkingFragment.class.getSimpleName();

    private List<ImageView> circles;
    private MediaPlayer mp;
    private ConfigurationFragment configurationFragment;
    private RetrieveData retrieveData;

    public static ParkingFragment newInstance(BluetoothSocket mmSocket, ConfigurationFragment configurationFragment) {
        ParkingFragment fragment = new ParkingFragment();
        fragment.bluetoothSocket = mmSocket;
        fragment.configurationFragment = configurationFragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_parking, container, false);

        circles = new ArrayList<>(4);
        circles.add((ImageView) view.findViewById(R.id.circle1));
        circles.add((ImageView) view.findViewById(R.id.circle2));
        circles.add((ImageView) view.findViewById(R.id.circle3));
        circles.add((ImageView) view.findViewById(R.id.circle4));

        mp = MediaPlayer.create(getContext(), R.raw.warning_sound);

        readData();

        return view;
    }

    public void readData() {
        final ParkingFragment ref = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Read the data
                retrieveData = new RetrieveDataParking(bluetoothSocket, getActivity(), ref);
                retrieveData.run();
            }
        }).start();
    }

    public void updateCircle(int distance) {
        Log.d(MainActivity.TAG, "Distance " + distance);
        for (int i = 0; i < distance; i++) {
            circles.get(i).setVisibility(View.GONE);
        }
        if (distance == 4) {
            mp.start();
        } else {
            if (mp.isPlaying() && getActivity() != null) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(getActivity(), R.raw.warning_sound);
            }
        }
        if (distance < 4) {
            for (int i = distance; i < 4; i++) {
                circles.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void close() {
        retrieveData.cancel();
        configurationFragment.init();
    }
}
