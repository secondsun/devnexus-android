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

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.PresentationContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.PresentationViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.SessionSpinnerAdaper;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.devnexus.vo.contract.PresentationContract.toQuery;

/**
 * Created by summers on 12/28/14.
 */
public class PresentationExplorerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SessionClickListener {

    private static final int SCHEDULE_LOADER = 0x0100;
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;
    private Toolbar toolbar;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = contentView = inflater.inflate(R.layout.schedule_layout, null);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        resolver = getActivity().getContentResolver();
        recycler.setAdapter(new ScheduleItemViewAdapter(new ArrayList<ScheduleItem>(1), getActivity()));

        spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(final Spinner spinner) {
        spinner.setAdapter(new SessionSpinnerAdaper(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SessionSpinnerAdaper.ITEMS item = (SessionSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();
                toolbar.setBackgroundColor(getResources().getColor(item.getRightDrawable()));
                switch (item) {

                    case ALL_EVENTS:
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, Bundle.EMPTY, PresentationExplorerFragment.this);
                        break;
                    case AGILE:
                    case CLOUD_DEVOPTS:
                    case DATA_INTEGRATION_IOT:
                    case FUNCTIONAL_PROGRAMMING:
                    case JAVA:
                    case JAVASCRIPT:
                    case JVM_LANGUAGES:
                    case MICROSERVICES_SECURITY:
                    case MOBILE:
                    case USER_EXPERIENCE_AND_TOOLS:
                    case WEB:
                        args.putString(PresentationContract.TRACK, getResources().getString(item.getTitleStringResource()));
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        SessionSpinnerAdaper.ITEMS item = (SessionSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(spinner.getSelectedItemPosition());
        args.putString(PresentationContract.TRACK, getResources().getString(item.getTitleStringResource()));
        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PresentationExplorerFragment.this);
    }

    public static Fragment newInstance() {
        return new PresentationExplorerFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args.isEmpty())
            return new CursorLoader(getActivity(), PresentationContract.URI, new String[]{}, null, null, null);
        else {
            String[] queryKeys = args.keySet().toArray(new String[]{});
            String[] queryValues = new String[queryKeys.length];
            for (int i = 0; i <queryKeys.length; i++) {
                queryValues[i] = args.getString(queryKeys[i]);
            }
            return new CursorLoader(getActivity(), PresentationContract.URI, new String[]{}, toQuery(queryKeys), queryValues, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Presentation> presentations = new ArrayList<>(data.getCount());
        while (data.moveToNext()) {
            Presentation presentation = GsonUtils.GSON.fromJson(data.getString(0), Presentation.class);
            presentations.add(presentation);
        }
        Collections.sort(presentations);
        refreshData(presentations);

    }

    private void refreshData(List<Presentation> presentationList) {
        recycler.setAdapter(new PresentationViewAdapter(presentationList, getActivity(), this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recycler.setAdapter(new PresentationViewAdapter(new ArrayList<Presentation>(1), getActivity(), this));
    }

    @Override
    public void loadSession(Presentation presentation) {
        Fragment sessionDetailFragment = SessionDetailFragment.newInstance(presentation.title, presentation.id);

        ((MainActivity)getActivity()).switchFragment(sessionDetailFragment, MainActivity.BackStackOperation.ADD, "SessionDetailFragment");
    }
}
