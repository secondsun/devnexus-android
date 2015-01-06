package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 12/23/14.
 */
public class DrawerFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.drawer_layout, null);

        view.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(PresentationExplorerFragment.newInstance(), MainActivity.BackStackOperation.NONE, "Open");
            }
        });

        view.findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).switchFragment(new GalleriaMapFragment(), MainActivity.BackStackOperation.NONE, "Open");
            }
        });

        return view;
    }
}
