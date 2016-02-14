package fr.isen.m2.elecauto.parkingsensors.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.isen.m2.elecauto.parkingsensors.R;

/**
 * Created by stephane on 14/02/16.
 */
public class AlarmNotSafeFragment extends Fragment {

    public static AlarmNotSafeFragment newInstance() {
        return new AlarmNotSafeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the view
        View view = inflater.inflate(R.layout.framgent_alarm_not_safe, container, false);

        return view;
    }
}
