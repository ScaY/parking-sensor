package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveData;
import fr.isen.m2.elecauto.parkingsensors.threads.RetrieveDataParking;

/**
 * Created by stephane on 08/02/16.
 */
public class ParkingFragment extends SocketFragment {

    private TextView tvDistance;
    private AnimationSet animation;

    public static ParkingFragment newInstance(BluetoothSocket mmSocket) {
        ParkingFragment fragment = new ParkingFragment();
        fragment.bluetoothSocket = mmSocket;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.fragment_parking, container, false);

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
                RetrieveData retrieveData = new RetrieveDataParking(bluetoothSocket, getActivity(),
                        animation, tvDistance);
                retrieveData.run();
            }
        }).start();
    }


}
