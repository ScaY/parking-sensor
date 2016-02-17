package fr.isen.m2.elecauto.parkingsensors.threads;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;
import fr.isen.m2.elecauto.parkingsensors.fragments.AlarmFragment;

/**
 * Created by stephane on 14/02/16.
 */
public class RetrieveDataAlarm extends RetrieveData {

    private FragmentActivity activity;
    private AlarmFragment alarmFragment;
    private List<Integer> previousValues;

    public RetrieveDataAlarm(BluetoothSocket socket, FragmentActivity activity, AlarmFragment alarmFragment) {
        super(socket);
        this.activity = activity;
        this.alarmFragment = alarmFragment;
        this.previousValues = new LinkedList<>();

    }

    @Override
    public void postAction(String value) {
        try {
            if(previousValues.size() > 10){
                previousValues.clear();
            }

            int distance = Integer.valueOf(value);
            previousValues.add(distance);

            if (hasBeenMoved()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alarmFragment.displayNotSafe();
                    }
                });
            }
        } catch (NumberFormatException e) {
            Log.d(MainActivity.TAG, "Exception when retrieved distance : " + e.getMessage());
        }
    }

    private boolean hasBeenMoved() {
        boolean result = false;
        for (int i = 0; i < previousValues.size() && !result; i++) {
            Integer value = previousValues.get(i);
            for (int j = i + 1; j < previousValues.size() && !result; j++) {
                if (Math.abs(value - previousValues.get(j)) > 3) {
                    result = true;
                }
            }
        }
        return result;
    }

}
