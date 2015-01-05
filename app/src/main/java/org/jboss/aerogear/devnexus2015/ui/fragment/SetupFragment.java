package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 12/26/14.
 */
public class SetupFragment extends Fragment {

    public SetupFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setup_layout, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        ContentResolver.setSyncAutomatically(SimpleDataAuthenticator.SIMPLE_ACCOUNT, "org.devnexus.sync", true);
        ContentResolver.addPeriodicSync(SimpleDataAuthenticator.SIMPLE_ACCOUNT,
                "org.devnexus.sync", new Bundle(), 3600);

        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();

        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);


        ContentResolver.requestSync(SimpleDataAuthenticator.SIMPLE_ACCOUNT,
                "org.devnexus.sync", settingsBundle);
    }

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }
}
