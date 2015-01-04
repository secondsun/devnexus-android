package org.jboss.aerogear.devnexus2015.ui;

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
import android.widget.Spinner;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.PresentationContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.PresentationViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SCHEDULE_LOADER = 0x0100;
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = contentView = inflater.inflate(R.layout.schedule_layout, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((MainActivity)getActivity()).attachToolbar(toolbar);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        resolver = getActivity().getContentResolver();
        recycler.setAdapter(new ScheduleItemViewAdapter(new ArrayList<ScheduleItem>(1)));

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(Spinner spinner) {
        spinner.setAdapter(new SessionSpinnerAdaper(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCHEDULE_LOADER, Bundle.EMPTY, this);
    }

    public static Fragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), PresentationContract.URI, new String[]{}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Presentation> presentations  = new ArrayList<>(data.getCount());
        while (data.moveToNext()) {
            Presentation presentation = GsonUtils.GSON.fromJson(data.getString(0), Presentation.class);
            presentations.add(presentation);
        }
        Collections.sort(presentations);
        refreshData(presentations);
        
    }

    private void refreshData(List<Presentation> presentationList) {
        recycler.setAdapter(new PresentationViewAdapter(presentationList));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recycler.setAdapter(new PresentationViewAdapter(new ArrayList<Presentation>(1)));
    }
}
