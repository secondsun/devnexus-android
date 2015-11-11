package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
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
public class SetupFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

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
                    ((MainActivity) getActivity()).switchFragment(PresentationExplorerFragment.newInstance(), MainActivity.BackStackOperation.RESET, "SessionListFragment");
                }
            }
        };
        
        resolver = getActivity().getContentResolver();
        return inflater.inflate(R.layout.setup_layout, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(0, Bundle.EMPTY, this);
        
    }

    @Override
    public void onStop() {
        super.onStop();
        resolver.unregisterContentObserver(presentationObersever);
    }

    public static SetupFragment newInstance(int launch) {
        Bundle attrs = new Bundle();
        attrs.putInt(MainActivity.LAUNCH_SCREEN, launch);
        SetupFragment setup = new SetupFragment();
        setup.setArguments(attrs);
        return setup;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), PresentationContract.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() <= 0) {
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
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int launch = getArguments().getInt(MainActivity.LAUNCH_SCREEN, MainActivity.LAUNCH_EXPLORE);
                    
                    Fragment fragment;
                    switch (launch){
                     default:
                         fragment = PresentationExplorerFragment.newInstance();
                            break;
                    }
                    ((MainActivity)getActivity()).switchFragment(fragment, MainActivity.BackStackOperation.RESET, "Open");
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
