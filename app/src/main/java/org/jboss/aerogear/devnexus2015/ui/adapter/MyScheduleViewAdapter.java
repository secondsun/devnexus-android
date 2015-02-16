package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.jboss.aerogear.devnexus2015.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by summers on 2/15/15.
 */
public class MyScheduleViewAdapter extends RecyclerView.Adapter<MyScheduleViewAdapter.ViewHolder>  {
    private final ArrayList<UserCalendar> calendars;
    private final Context context;

    public MyScheduleViewAdapter(ArrayList<UserCalendar> userCalendars, Context context) {
        this.calendars = userCalendars;
        this.context = context.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usercalendar_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserCalendar userCalendarItem = calendars.get(position);
        
        holder.time = userCalendarItem.fromTime;
        
        if (userCalendarItem.item == null) {
            holder.title = "Select a Session >";
            holder.imageUrl = "";
            holder.color = context.getResources().getColor(R.color.dn_white);
        } else {
            ScheduleItem item = userCalendarItem.item;
            holder.title = item.presentation.title;
            holder.imageUrl = "https://devnexus.com/s/speakers/"+item.presentation.speakers.get(0).id+".jpg";
            holder.color =  context.getResources().getColor(TrackRoomUtil.forTrack(item.presentation.track.name));
        }
    }

    @Override
    public int getItemCount() {
        return calendars.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        String title;
        Date time;
        String imageUrl;
        int color;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
