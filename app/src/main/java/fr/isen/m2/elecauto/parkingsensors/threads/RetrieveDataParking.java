package fr.isen.m2.elecauto.parkingsensors.threads;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.view.animation.AnimationSet;
import android.widget.TextView;

/**
 * Created by stephane on 14/02/16.
 */
public class RetrieveDataParking extends RetrieveData {

    private Activity activity;
    private AnimationSet animationSet;
    private TextView textView;

    public RetrieveDataParking(BluetoothSocket socket, Activity activity, AnimationSet animationSet,
                               TextView textView) {
        super(socket);
        this.activity = activity;
        this.animationSet = animationSet;
        this.textView = textView;
    }

    @Override
    public void postAction(final String finalValue) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Start the animation.
                animationSet.start();
                textView.setText(finalValue);
            }
        });

    }
}
