package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.ScheduleContract;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 1/3/14.
 */
public class RoomViewFragment extends DialogFragment {

    private static final String ROOM_NAME = "TrackViewFragment.ROOM_NAME";
    private static final String TAG = RoomViewFragment.class.getSimpleName();

    private ScheduleItemViewAdapter adapter;

    private List<ScheduleItem> schedule;
    private RecyclerView listView;
    private static final Gson GSON = GsonUtils.GSON;
    private ProgressBar progress;


    public static RoomViewFragment newInstance(String roomName) {
        Bundle args = new Bundle();

        args.putString(ROOM_NAME, roomName);
        RoomViewFragment frag = new RoomViewFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);


        if (adapter == null) {
            adapter = new ScheduleItemViewAdapter(new ArrayList<ScheduleItem>(0));
        }

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {

                Bundle args = getArguments();
                String trackName = args.getString(ROOM_NAME);

                if (schedule == null) {
                    Cursor cursor = null;
                    schedule = new ArrayList<ScheduleItem>(10);
                    try {
                        cursor = getActivity().getContentResolver().query(ScheduleContract.URI, null, null, null, null);

                        if (cursor != null && cursor.moveToNext()) {
                            Schedule scheduleFromDb = GSON.fromJson(cursor.getString(0), Schedule.class);
                            for (ScheduleItem scheduleItem : scheduleFromDb.scheduleItemList.scheduleItems) {
                                Log.d("Room", scheduleItem.room.name);
                                if (scheduleItem.room.name.equals(trackName)) {
                                    schedule.add(scheduleItem);
                                }
                            }
                        } else {
                            //???
                        }

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>(schedule.size());

                for (ScheduleItem item : schedule) {
                    scheduleItems.add(item);
                }

                adapter = new ScheduleItemViewAdapter(scheduleItems);
                adapter.notifyDataSetChanged();
                if (listView != null) {
                    listView.setAdapter(adapter);
                    progress.setVisibility(View.GONE);
                    listView.requestLayout();
                    listView.refreshDrawableState();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getArguments().getString(ROOM_NAME));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.session_picker, null);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        listView = (RecyclerView) view.findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                UserCalendar userCal = new UserCalendar();
//                userCal.fixed = true;
//                SessionDetailFragment.newInstance(userCal, adapter.getItem(position)).show(getActivity().getSupportFragmentManager(), TAG);
//            }
//        });


        return view;
    }
}
