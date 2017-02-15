package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.ScheduleItemContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemWithHeaderViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.SessionSpinnerAdaper;
import org.jboss.aerogear.devnexus2015.ui.adapter.TimeSpinnerAdaper;
import org.jboss.aerogear.devnexus2015.util.CenteringDecoration;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.devnexus.vo.contract.ScheduleItemContract.toQuery;

/**
 * Created by summers on 12/28/14.
 */
public class PresentationExplorerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SessionClickListener {

    private static final int SCHEDULE_LOADER = 0x0100;
    @Nullable
    @Bind(R.id.my_recycler_view)
    RecyclerView recycler;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.track_spinner)
    Spinner topicsSpinner;
    @Bind(R.id.date_spinner)
    Spinner timeSpinner;

    private ArrayList<ScheduleItem> sechduleItemList = new ArrayList<>(1);
    private int columnCount = 3;
    private int scrollPosition = 0;

    public static Fragment newInstance() {
        PresentationExplorerFragment fragment = new PresentationExplorerFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule_layout, null);
        ButterKnife.bind(this, view);


        toolbar.setTitle("");
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        columnCount = ((MainActivity) getActivity()).getColumnCount();

        recycler.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));
        recycler.setAdapter(new ScheduleItemWithHeaderViewAdapter(sechduleItemList, getActivity(), columnCount));
        ((GridLayoutManager) recycler.getLayoutManager()).setSpanSizeLookup(((ScheduleItemWithHeaderViewAdapter) recycler.getAdapter()).getSpanSizeLookup());

        recycler.addItemDecoration(new CenteringDecoration(columnCount, 280, getActivity()));

        loadTopicSpinner(topicsSpinner);
        loadTimeSpinner(this.timeSpinner);
        toolbar.setBackgroundColor(getResources().getColor(R.color.dn_white));
        return view;
    }

    private void loadTimeSpinner(final Spinner spinner) {
        spinner.setAdapter(new TimeSpinnerAdaper(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeSpinnerAdaper.ITEMS item = (TimeSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();

                switch (item) {

                    case ALL_TIME:
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, Bundle.EMPTY, PresentationExplorerFragment.this);
                        break;
                    case SPACER_0:
                    case SPACER_1:
                    case SPACER_2:
                        break;

                    case WEDNESDAY:
                    case WED_9_AM:
                    case THURSDAY:
                    case THURS_9_AM:
                    case THURS_10_30_AM:
                    case THURS_1_PM:
                    case THURS_2_30_PM:
                    case THURS_4_PM:
                    case THURS_5_30_PM:
                    case FRIDAY:
                    case FRI_9_AM:
                    case FRI_10_30_AM:
                    case FRI_1_PM:
                    case FRI_2_30_PM:
                    case FRI_4_PM:
                        args.putString(ScheduleItemContract.FROM_TIME, item.getFromTime().getTime() + "");
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PresentationExplorerFragment.this);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadTopicSpinner(final Spinner spinner) {
        spinner.setAdapter(new SessionSpinnerAdaper(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SessionSpinnerAdaper.ITEMS item = (SessionSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();

                switch (item) {

                    case ALL_EVENTS:
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, Bundle.EMPTY, PresentationExplorerFragment.this);
                        break;

                    case SPACER_0:
                        break;
                    case SESSION_TOPIC:
                        break;
                    case WORKSHOP:
                    case KEYNOTE:
                    case MISC:
                    case C3P0:
                    case R2D2:
                    case FRAMEWORKS:
                    case JAVA:
                    case LANGUAGES:
                    case PERFORMANCE:
                    case TOOLS:
                    case WEB:
                    case CLOUD:
                    case MICROSERVICES:
                    case SECURITY:
                    case ARCHITECTURE:
                    case JAVASCRIPT:
                    case MOBILE:
                    case DATA_SCIENCE:
                    case AGILE:
                        args.putString(ScheduleItemContract.TRACK, getResources().getString(item.getTitleStringResource()));
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PresentationExplorerFragment.this);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCHEDULE_LOADER, Bundle.EMPTY, PresentationExplorerFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.recycler.getLayoutManager().scrollToPosition(scrollPosition);
        ((ScheduleItemWithHeaderViewAdapter) recycler.getAdapter()).setClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((ScheduleItemWithHeaderViewAdapter) recycler.getAdapter()).setClickListener(null);
        try {
            scrollPosition = ((GridLayoutManager) this.recycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args.isEmpty())
            return new CursorLoader(getActivity(), ScheduleItemContract.URI, new String[]{}, null, null, null);
        else {
            String[] queryKeys = args.keySet().toArray(new String[]{});
            String[] queryValues = new String[queryKeys.length];
            for (int i = 0; i < queryKeys.length; i++) {
                queryValues[i] = args.getString(queryKeys[i]);
            }
            return new CursorLoader(getActivity(), ScheduleItemContract.URI, new String[]{}, toQuery(queryKeys), queryValues, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<ScheduleItem> scheduleItems = new ArrayList<>(data.getCount());
        while (data.moveToNext()) {
            ScheduleItem presentation = GsonUtils.GSON.fromJson(data.getString(0), ScheduleItem.class);
            scheduleItems.add(presentation);
        }

        List<ScheduleItem> nonPresentationItems = new ArrayList<>(scheduleItems.size());
        for (ScheduleItem item : scheduleItems) {
            if (item.presentation == null) {
                nonPresentationItems.add(item);
            }
        }

        scheduleItems.removeAll(nonPresentationItems);

        Collections.sort(scheduleItems);
        if (scheduleItems.isEmpty()) {
            Bundle settingsBundle = new Bundle();

            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            ContentResolver.requestSync(SimpleDataAuthenticator.SIMPLE_ACCOUNT,
                    "org.devnexus.sync", settingsBundle);

        }
        refreshData(scheduleItems);

    }

    private void refreshData(List<ScheduleItem> presentationList) {
        if (sechduleItemList.equals(presentationList) || presentationList.isEmpty()) {
            this.recycler.getLayoutManager().scrollToPosition(scrollPosition);
            return;
        }
        scrollPosition = 0;
        this.sechduleItemList = new ArrayList<>(presentationList);

        ScheduleItemWithHeaderViewAdapter adapter = new ScheduleItemWithHeaderViewAdapter(presentationList, getActivity(), columnCount, false, this);
        recycler.setAdapter(adapter);
        ((GridLayoutManager) recycler.getLayoutManager()).setSpanSizeLookup(((ScheduleItemWithHeaderViewAdapter) recycler.getAdapter()).getSpanSizeLookup());
        scrollPosition = adapter.getDateIndex(new Date());
        if (scrollPosition != 0) {
            scrollPosition--;
        }
        recycler.scrollToPosition(scrollPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.sechduleItemList = new ArrayList<>(1);
        if (recycler != null) {
            recycler.setAdapter(new ScheduleItemWithHeaderViewAdapter(this.sechduleItemList, getActivity(), columnCount, false, this));
            ((GridLayoutManager) recycler.getLayoutManager()).setSpanSizeLookup(((ScheduleItemWithHeaderViewAdapter) recycler.getAdapter()).getSpanSizeLookup());
        }

    }

    @Override
    public void loadSession(Presentation presentation) {
        Fragment sessionDetailFragment = SessionDetailFragment.newInstance(presentation.title, presentation.id);

        ((MainActivity) getActivity()).switchFragment(sessionDetailFragment, MainActivity.BackStackOperation.ADD, "SessionDetailFragment");
    }
}
