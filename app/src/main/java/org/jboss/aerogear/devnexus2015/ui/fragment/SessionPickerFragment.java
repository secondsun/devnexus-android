package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.ScheduleItemContract;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.PresentationViewAdapter;
import org.jboss.aerogear.devnexus2015.util.CenteringDecoration;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;
import org.jboss.aerogear.devnexus2015.util.SessionPickerReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by summers on 12/11/13.
 */
public class SessionPickerFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final DateFormat format = new SimpleDateFormat("h:mm a");


    private static final String CALENDAR = "SessionPickerFragment.CALENDAR";
    private static final String DATE = "SessionPickerFragment.DATE";
    private static final int PICKER_LOADER = 0x100;
    private static final String START_TIME_PARAM = "START_TIME";
    private PresentationViewAdapter adapter;
    private UserCalendar calendarItem;
    private SessionPickerReceiver receiver;
    private RecyclerView listView;

    public static SessionPickerFragment newInstance(UserCalendar calendarItem) {
        SessionPickerFragment fragment = new SessionPickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATE, calendarItem.fromTime);
        args.putSerializable(CALENDAR, calendarItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Sessions");
        return dialog;
    }

    public void setReceiver(SessionPickerReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        Bundle args = getArguments();
        calendarItem = (UserCalendar) args.getSerializable(CALENDAR);

        if (activity instanceof SessionPickerReceiver) {
            this.receiver = (SessionPickerReceiver) activity;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = new Bundle();
        args.putSerializable(START_TIME_PARAM, calendarItem.fromTime);
        
        getLoaderManager().initLoader(PICKER_LOADER, args, this);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.session_picker, null);
        
        listView = (RecyclerView) view.findViewById(R.id.listView);
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        listView.setVisibility(View.VISIBLE);
        if (adapter == null) {
            adapter = new PresentationViewAdapter(new ArrayList<Presentation>(0), getActivity(), new SessionClickListener() {
                @Override
                public void loadSession(Presentation presentation) {
                    ScheduleItem item = new ScheduleItem();
                    item.presentation = presentation;
                    receiver.receiveSessionItem(calendarItem, item);
                    dismiss();
                }
            });
        }
        listView.addItemDecoration(new CenteringDecoration(1, 230, getActivity()));

        listView.setAdapter(adapter);
        listView.requestLayout();
        listView.refreshDrawableState();
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = ScheduleItemContract.FROM_TIME;
        String[] params = {((Date)args.getSerializable(START_TIME_PARAM)).getTime() + ""};
        return new CursorLoader(getActivity(), ScheduleItemContract.URI, null, query, params, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<ScheduleItem> scheduleItems = new ArrayList<>(data.getCount());
        while (data.moveToNext()) {
            ScheduleItem presentation = GsonUtils.GSON.fromJson(data.getString(0), ScheduleItem.class);
            scheduleItems.add(presentation);
        }
        Collections.sort(scheduleItems);
        refreshData(scheduleItems);
    }

    private void refreshData(List<ScheduleItem> scheduleItems) {

        List<Presentation> presentations = new ArrayList<>(scheduleItems.size());
        for (ScheduleItem item :scheduleItems) {
            presentations.add(item.presentation);
        }
        adapter = new PresentationViewAdapter(presentations, getActivity(), new SessionClickListener() {
            @Override
            public void loadSession(Presentation presentation) {
                ScheduleItem item = new ScheduleItem();
                item.presentation = presentation;
                receiver.receiveSessionItem(calendarItem, item);
            }
        });
        Log.d("AdapterFinished", adapter.toString());
        listView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
