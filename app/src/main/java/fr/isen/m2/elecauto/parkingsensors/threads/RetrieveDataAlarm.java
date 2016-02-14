package fr.isen.m2.elecauto.parkingsensors.threads;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Scanner;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.activities.MainActivity;
import fr.isen.m2.elecauto.parkingsensors.fragments.AlarmNotSafeFragment;

/**
 * Created by stephane on 14/02/16.
 */
public class RetrieveDataAlarm extends RetrieveData {

    private FragmentActivity activity;
    private int previousValue;

    public RetrieveDataAlarm(BluetoothSocket socket, FragmentActivity activity) {
        super(socket);
        this.activity = activity;
        previousValue = -1;
    }

    @Override
    public void postAction(String value) {

        if (previousValue == -1) {
            previousValue = Integer.valueOf(value);
        }

        if (isInteger(value, 10) && previousValue != Integer.valueOf(value)) {
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, AlarmNotSafeFragment.newInstance());
            ft.addToBackStack(MainActivity.TAG);
            ft.commit();
        }
    }

    private boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if (!sc.hasNextInt(radix)) return false;
        // we know it starts with a valid int, now make sure
        // there's nothing left!
        sc.nextInt(radix);
        boolean result = !sc.hasNext();
        sc.close();
        return result;
    }
}
