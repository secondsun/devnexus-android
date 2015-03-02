package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jboss.aerogear.devnexus2015.GoogleConnectActivity;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.DrawerItem;
import org.jboss.aerogear.devnexus2015.ui.adapter.DrawerListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.jboss.aerogear.devnexus2015.MainActivity.BackStackOperation.*;

public class DevNexusDrawerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_layout, null);

        List<DrawerItem> list1Items = new ArrayList<>();

        ListView list1 = (ListView) view.findViewById(R.id.list1);
        list1.setAdapter(new DrawerListAdapter(getActivity(), list1Items));
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = (MainActivity) getActivity();
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(getActivity(), GoogleConnectActivity.class));
                        break;
                    case 1:
                        // TODO
                        break;
                }
                activity.closeDrawer();
            }
        });

        List<DrawerItem> list2Items = new ArrayList<>();
        list2Items.add(new DrawerItem(R.drawable.ic_action_book, "All Presentations"));
        list2Items.add(new DrawerItem(R.drawable.ic_action_calendar_day, "My Schedule"));
        list2Items.add(new DrawerItem(R.drawable.ic_action_map, "Map"));
        list2Items.add(new DrawerItem(R.drawable.ic_action_dev, "Previous Year"));
        list2Items.add(new DrawerItem(R.drawable.ic_action_headphones, "Podcasts"));
        list2Items.add(new DrawerItem(R.drawable.ic_action_video, "InfoQ Videos"));

        ListView list2 = (ListView) view.findViewById(R.id.list2);
        list2.setAdapter(new DrawerListAdapter(getActivity(), list2Items));
        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity activity = (MainActivity) getActivity();
                switch (position) {
                    case 0:
                        activity.switchFragment(PresentationExplorerFragment.newInstance(), RESET, "Open");
                        break;
                    case 1:
                        activity.switchFragment(new MyScheduleFragment(), RESET, "Schedule");
                        break;
                    case 2:
                        activity.switchFragment(new GalleriaMapFragment(), RESET, "Map");
                        break;
                    case 3:
                        activity.switchFragment(PreviousConferencesFragment.newInstance(), RESET, "Previous");
                        break;
                    case 4:
                        activity.switchFragment(PodcastFragment.newInstance(), RESET, "Podcast");
                        break;
                    case 5:
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.infoq.com/devnexus/"));
                        activity.startActivity(myIntent);
                        break;
                }
                activity.closeDrawer();
            }
        });

        return view;

    }

}
