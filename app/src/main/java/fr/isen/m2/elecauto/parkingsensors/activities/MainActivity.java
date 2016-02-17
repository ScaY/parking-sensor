package fr.isen.m2.elecauto.parkingsensors.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import fr.isen.m2.elecauto.parkingsensors.R;
import fr.isen.m2.elecauto.parkingsensors.fragments.ClosingFragment;
import fr.isen.m2.elecauto.parkingsensors.fragments.ConfigurationFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "HOME_TAG";
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, ConfigurationFragment.newInstance());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentFragment instanceof ClosingFragment) {
            Log.d(TAG, "Closing the fragment");
            ((ClosingFragment) currentFragment).close();
        } else {
            Log.d(TAG, "Go damn it");
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        currentFragment = fragment;

    }
}




