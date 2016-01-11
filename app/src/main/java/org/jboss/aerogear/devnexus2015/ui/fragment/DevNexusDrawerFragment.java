package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.DrawerItem;
import org.jboss.aerogear.devnexus2015.ui.adapter.DrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.aerogear.devnexus2015.MainActivity.BackStackOperation.RESET;

public class DevNexusDrawerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_layout, null);

        List<DrawerItem> list2Items = new ArrayList<>();
        list2Items.add(new DrawerItem(R.drawable.ic_presentations, "All Presentations"));
        list2Items.add(new DrawerItem(R.drawable.ic_schedule, "My Schedule"));
        list2Items.add(new DrawerItem(R.drawable.ic_map, "Map"));
        list2Items.add(new DrawerItem(R.drawable.ic_person_add, "Scan Badge"));
        list2Items.add(new DrawerItem(R.drawable.ic_info, "About"));

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
                        activity.switchFragment(new VenueMapFragment(), RESET, "Map");
                        break;
                    case 3:
                        activity.switchFragment(BadgeContactsFragment.newInstance(), RESET, "BadgeContact");
                        break;
                    case 4:
                        activity.switchFragment(AboutFragment.newInstance(), RESET, "About");
                        break;
                }
                activity.closeDrawer();
            }
        });

        return view;

    }

}
