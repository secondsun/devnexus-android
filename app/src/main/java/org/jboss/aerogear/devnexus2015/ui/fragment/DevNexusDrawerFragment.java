package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jboss.aerogear.devnexus2015.GoogleConnectActivity;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 12/23/14.
 */
public class DevNexusDrawerFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.drawer_layout, null);

        view.findViewById(R.id.presentations).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(PresentationExplorerFragment.newInstance(), MainActivity.BackStackOperation.RESET, "Open");
                ((MainActivity)getActivity()).closeDrawer();
            }
        });

        view.findViewById(R.id.map_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(new GalleriaMapFragment(), MainActivity.BackStackOperation.RESET, "Map");
                ((MainActivity)getActivity()).closeDrawer();
            }
        });

        view.findViewById(R.id.infoq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.infoq.com/devnexus/"));
                ((MainActivity)getActivity()).startActivity(myIntent);
                ((MainActivity)getActivity()).closeDrawer();
            }
        });

        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).startActivity(new Intent(getActivity(), GoogleConnectActivity.class));
                ((MainActivity)getActivity()).closeDrawer();
            }
        });

        view.findViewById(R.id.podcast_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(PodcastFragment.newInstance(), MainActivity.BackStackOperation.RESET, "{Pdcast");
                ((MainActivity)getActivity()).closeDrawer();
            }
        });


        view.findViewById(R.id.previous_conference_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(PreviousConferencesFragment.newInstance(), MainActivity.BackStackOperation.RESET, "Previous");
                ((MainActivity)getActivity()).closeDrawer();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
