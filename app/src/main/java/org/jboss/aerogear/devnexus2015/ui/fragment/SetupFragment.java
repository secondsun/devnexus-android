package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.devnexus.vo.contract.PresentationContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 12/26/14.
 */
public class SetupFragment extends Fragment {

    private ContentObserver presentationObersever;
    private ContentResolver resolver;

    public SetupFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        presentationObersever = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).switchFragment(PresentationExplorerFragment.newInstance(), MainActivity.BackStackOperation.ADD, "SessionListFragment");
                }
            }
        };
        resolver = getActivity().getContentResolver();
        return inflater.inflate(R.layout.setup_layout, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().getContentResolver().registerContentObserver(PresentationContract.URI, false, presentationObersever);


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

    @Override
    public void onStop() {
        super.onStop();
        resolver.unregisterContentObserver(presentationObersever);
    }

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }
}
