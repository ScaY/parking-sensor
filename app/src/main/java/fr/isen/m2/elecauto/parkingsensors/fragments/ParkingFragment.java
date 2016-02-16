package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveData;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveDataParking;

/**
 * Created by stephane on 08/02/16.
 */
public class ParkingFragment extends SocketFragment {

    private TextView tvDistance;
    private AnimationSet animation;
    private List<ImageView> circles;
    private MediaPlayer mp;

    public static ParkingFragment newInstance(BluetoothSocket mmSocket) {
        ParkingFragment fragment = new ParkingFragment();
        fragment.bluetoothSocket = mmSocket;
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
                RetrieveData retrieveData = new RetrieveDataParking(bluetoothSocket, getActivity(), ref);
                retrieveData.run();
            }
        }).start();
    }

    public void updateCircle(int distance) {
        Log.d(MainActivity.TAG, "Distance " + distance);
        distance = Math.min(distance, 4);
        for (int i = 0; i < distance; i++) {
            circles.get(i).setVisibility(View.VISIBLE);
        }
        if (distance == 1) {
            mp.start();
        } else {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = MediaPlayer.create(getActivity(), R.raw.warning_sound);
            }
        }
        if (distance < 4) {
            for (int i = distance; i < 4; i++) {
                circles.get(i).setVisibility(View.GONE);
            }
        }
    }

}
