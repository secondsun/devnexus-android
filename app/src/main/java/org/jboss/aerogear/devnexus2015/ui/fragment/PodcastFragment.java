package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.devnexus.util.JsonLoader;
import org.devnexus.vo.Podcast;
import org.devnexus.vo.PodcastList;
import org.devnexus.vo.ScheduleItem;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.PodcastClickListener;
import org.jboss.aerogear.devnexus2015.ui.adapter.PodcastSpinnerAdaper;
import org.jboss.aerogear.devnexus2015.ui.adapter.PodcastViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by summers on 1/7/15.
 */
public class PodcastFragment extends Fragment implements LoaderManager.LoaderCallbacks<PodcastList>,PodcastClickListener {

    private static final int SCHEDULE_LOADER = 0x0100;
    private static final String TRACK = "PodcastFragment.trackName";
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = contentView = inflater.inflate(R.layout.schedule_layout, null);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        resolver = getActivity().getContentResolver();
        recycler.setAdapter(new ScheduleItemViewAdapter(new ArrayList<ScheduleItem>(1), getActivity(), true));

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(final Spinner spinner) {
        spinner.setAdapter(new PodcastSpinnerAdaper(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PodcastSpinnerAdaper.ITEMS item = (PodcastSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();
                toolbar.setBackgroundColor(getResources().getColor(item.getRightDrawable()));
                switch (item) {

                    case AGILE:
                    case CLOUD_DEVOPTS:
                    case DATA_INTEGRATION_IOT:
                    case FUNCTIONAL_PROGRAMMING:
                    case HTML_5:
                    case JAVA:
                    case JAVASCRIPT:
                    case JVM_LANGUAGES:
                    case MICROSERVICES_SECURITY:
                    case MOBILE:
                    case USER_EXPERIENCE_AND_TOOLS:
                    case WEB:
                        args.putString(TRACK, getResources().getString(item.getTitleStringResource()));
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PodcastFragment.this).forceLoad();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCHEDULE_LOADER, Bundle.EMPTY, this).forceLoad();
    }

    public static Fragment newInstance() {
        return new PodcastFragment();
    }

    @Override
    public Loader<PodcastList> onCreateLoader(int id, Bundle args) {
        return new JsonLoader<PodcastList>(PodcastList.class, R.raw.podcasts, getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<PodcastList> loader, PodcastList data) {
        
        List<Podcast> finalList = new ArrayList<>();
        List<Podcast> podcasts = data.podcasts;
        Bundle args = ((JsonLoader)loader).getArgs();
        if (args.isEmpty()) {
            finalList =podcasts;
        } else {
            for (Podcast podcast : podcasts) {
                if (podcast.track.equals(args.getString(TRACK))) {
                    finalList.add(podcast);
                }
            }
        }
        
        Collections.sort(finalList);
        refreshData(finalList);

    }

    private void refreshData(List<Podcast> presentationList) {
        recycler.setAdapter(new PodcastViewAdapter(presentationList, getActivity(), this));
    }

    @Override
    public void onLoaderReset(Loader<PodcastList> loader) {
        recycler.setAdapter(new PodcastViewAdapter(new ArrayList<Podcast>(1), getActivity(), this));
    }

    @Override
    public void loadSession(Podcast presentation) {
        Fragment sessionDetailFragment = PodcastPlayerFragment.newInstance(presentation.title, presentation.id, presentation.link, presentation.track);

        ((MainActivity)getActivity()).switchFragment(sessionDetailFragment, MainActivity.BackStackOperation.ADD, "SessionDetailFragment");
    }
}
