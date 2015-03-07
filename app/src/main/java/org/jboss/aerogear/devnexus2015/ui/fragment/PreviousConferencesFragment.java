package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.contract.PreviousYearPresentationContract;
import org.devnexus.vo.contract.PreviousYearPresentationContract.Events;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.PresentationViewAdapter;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.devnexus.vo.contract.PreviousYearPresentationContract.toQuery;

/**
 * Created by summers on 1/7/15.
 */
public class PreviousConferencesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SessionClickListener {

    private static final int SCHEDULE_LOADER = 0x0100;
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;
    private Toolbar toolbar;

    private Events event = Events.DEVNEXUX2014;

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
        recycler.setAdapter(new PresentationViewAdapter(new ArrayList<Presentation>(1), getActivity(), this));

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(final Spinner spinner) {
        spinner.setAdapter(new ArrayAdapter<Events>(getActivity(), R.layout.textview){
            @Override
            public Events getItem(int position) {
                return Events.values()[position];
            }

            @Override
            public int getCount() {
                return Events.values().length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.textview_dropdown, null);
                }

                ((TextView)convertView.findViewById(R.id.header_label)).setText(getItem(position).getLabel());

                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.textview, null);
                }
                
                ((TextView)convertView.findViewById(R.id.header_label)).setText(getItem(position).getLabel());
                
                return convertView;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Events item = (Events) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();
                event = item;
                args.putString(PreviousYearPresentationContract.EVENT_LABEL, item.getLabel());
                getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PreviousConferencesFragment.this);
        
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
        args.putString(PreviousYearPresentationContract.EVENT_LABEL, event.getLabel());
        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args.isEmpty())
            return new CursorLoader(getActivity(), PreviousYearPresentationContract.URI, new String[]{}, null, null, null);
        else {
            String[] queryKeys = args.keySet().toArray(new String[]{});
            String[] queryValues = new String[queryKeys.length];
            for (int i = 0; i < queryKeys.length; i++) {
                queryValues[i] = args.getString(queryKeys[i]);
            }
            return new CursorLoader(getActivity(), PreviousYearPresentationContract.URI, new String[]{}, toQuery(queryKeys), queryValues, null);
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
        Fragment sessionDetailFragment = SessionDetailFragment.newInstance(presentation.title, presentation.id, PreviousYearPresentationContract.URI, this.event.getLabel());

        ((MainActivity) getActivity()).switchFragment(sessionDetailFragment, MainActivity.BackStackOperation.ADD, "SessionDetailFragment");
    }


    public static Fragment newInstance() {
        return new PreviousConferencesFragment();
    }


}
