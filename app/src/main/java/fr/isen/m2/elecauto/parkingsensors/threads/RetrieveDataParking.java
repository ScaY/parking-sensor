package fr.isen.m2.elecauto.parkingsensors.threads;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;
import fr.isen.m2.elecauto.parkingsensors.fragments.ParkingFragment;

/**
 * Created by stephane on 14/02/16.
 */
public class RetrieveDataParking extends RetrieveData {

    private Activity activity;
    private ParkingFragment fragment;

    public RetrieveDataParking(BluetoothSocket socket, Activity activity, ParkingFragment fragment) {
        super(socket);
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public void postAction(final String finalValue) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    int distance = Integer.parseInt(finalValue);
                    if(distance > 0 && distance < 5){
                        fragment.updateCircle(distance);
                    }
                }catch (NumberFormatException e){
                    Log.d(MainActivity.TAG, "Bad distance");
                }
            }
        });

    }
}
