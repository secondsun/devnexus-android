package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.BadgeContact;
import org.jboss.aerogear.devnexus2015.ui.adapter.BadgeContactViewAdapter;
import org.jboss.aerogear.devnexus2015.util.CenteringDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment for managing, adding, removing, and exporting badge contacts
 */
public class BadgeContactsFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    @Nullable
    @Bind(R.id.my_recycler_view)
    RecyclerView recycler;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public BadgeContactsFragment() {
        // Required empty public constructor
    }

    public static BadgeContactsFragment newInstance() {
        BadgeContactsFragment fragment = new BadgeContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.badge_contacts, container, false);
        ButterKnife.bind(this, view);


        ((MainActivity) getActivity()).attachToolbar(toolbar);
        toolbar.setTitle("Badge Contacts");
        setHasOptionsMenu(true);
        int columnCount = ((MainActivity) getActivity()).getColumnCount();

        recycler.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));
        recycler.setAdapter(new BadgeContactViewAdapter(new ArrayList<BadgeContact>(0)));

        recycler.addItemDecoration(new CenteringDecoration(columnCount, 220, getActivity()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.badge_contacts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                break;
            case R.id.action_scan:
                ((MainActivity) getActivity()).launchBarcodeScanner();
                return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                break;
            case R.id.action_scan:
                ((MainActivity) getActivity()).launchBarcodeScanner();
                return true;
        }
        return false;
    }
}
