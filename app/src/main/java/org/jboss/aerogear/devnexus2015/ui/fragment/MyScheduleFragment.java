package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.UserCalendarContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.devnexus.vo.contract.UserCalendarContract.DATE;

/**
 * Created by summers on 1/7/15.
 */
public class MyScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final List<Date> DATES = Arrays.asList(new Date[]{asDate(Calendar.MARCH, 10, 2015), asDate(Calendar.MARCH, 11, 2015), asDate(Calendar.MARCH, 12, 2015)});
    private static final int SCHEDULE_LOADER = 0x0100;
    private static final String DATE_KEY = "Schedule.dateKey";
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = contentView = inflater.inflate(R.layout.my_schedule, null);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        resolver = getActivity().getContentResolver();
        recycler.setAdapter(new MyScheduleViewAdapter(new ArrayList<UserCalendar>(1), getActivity()));

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(final Spinner spinner) {
        spinner.setAdapter(new CalendarDateAdapter(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
                Bundle args = new Bundle();
                args.putInt(DATE_KEY, position);
                getLoaderManager().restartLoader(SCHEDULE_LOADER, Bundle.EMPTY, MyScheduleFragment.this);
                

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args == null) {
            args = Bundle.EMPTY;
        }
        int dateIndex = args.getInt(DATE_KEY, 0);

        return new CursorLoader(getActivity(), UserCalendarContract.URI, null, DATE, new String[]{"" +dateIndex}, null);
        
        
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class MyScheduleViewAdapter extends RecyclerView.Adapter {
        public MyScheduleViewAdapter(ArrayList<UserCalendar> scheduleItems, Activity activity) {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class CalendarDateAdapter extends BaseAdapter implements SpinnerAdapter {

        

        private final DateFormat FORMAT = new SimpleDateFormat("MMMM dd");

        public CalendarDateAdapter(Activity activity) {
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return DATES.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.textview, null);
            }

            ((TextView) convertView.findViewById(R.id.header_label)).setText(FORMAT.format((Date) getItem(position)));

            return convertView;
        }
    }

    private static Date asDate(int month, int day, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        return cal.getTime();
    }

}
