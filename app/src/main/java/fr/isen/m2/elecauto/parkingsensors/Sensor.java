package fr.isen.m2.elecauto.parkingsensors;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.isen.m2.elecauto.parkingsensors.view.ConeView;

public class Sensor extends ActionBarActivity {


    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout view = (RelativeLayout) findViewById(R.id.sensorRelativeLayout);
 /*       setContentView(R.layout.activity_sensor);

        // Retrieving the several view elements
        tvData = (TextView) findViewById(R.id.sensor_tv_distance);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) tvData.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();*/


        ConeView coneView = new ConeView(this);

        view.addView(coneView);


    }



}
