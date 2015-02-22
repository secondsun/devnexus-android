package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.jboss.aerogear.devnexus2015.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by summers on 2/15/15.
 */
public class MyScheduleViewAdapter extends RecyclerView.Adapter<MyScheduleViewAdapter.ViewHolder>  {
    private final ArrayList<UserCalendar> calendars;
    private final Context context;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    
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
        
        holder.startTime.setText(TIME_FORMAT.format(userCalendarItem.fromTime));
        holder.endTime.setText(TIME_FORMAT.format(userCalendarItem.getToTime()));
        
        if (userCalendarItem.item == null &&!userCalendarItem.fixed) {
            holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_white)));
            holder.title.setText( "Select a Session ");
            holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
            holder.titleBar.setBackgroundColor(context.getResources().getColor(R.color.dn_white));
            holder.feedbackButton.setVisibility(View.GONE);
        } else {
            ScheduleItem item = userCalendarItem.item;
            if (item!= null) {
                int color = context.getResources().getColor(TrackRoomUtil.forTrack(item.presentation.track.name));
                holder.title.setText(item.presentation.title);
                holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                Picasso.with(context).load("https://devnexus.com/s/speakers/"+item.presentation.speakers.get(0).id+".jpg").into(holder.image);
                holder.titleBar.setBackgroundColor(color);
                holder.feedbackButton.setVisibility(View.VISIBLE);


                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                
                double lightness = Math.sqrt(r*r*.299 + g*g*.587 + b*b*.114)/255d;//Calculate luminance 
                
                if (lightness >.5d) {
                    holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));    
                } else {
                    holder.title.setTextColor(context.getResources().getColor(R.color.dn_white));
                }
                
            } else {
                holder.title.setText(userCalendarItem.fixedTitle);
                holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                holder.titleBar.setBackgroundColor(context.getResources().getColor(R.color.dn_white));
                holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_white)));
                holder.feedbackButton.setVisibility(View.GONE);
            }
            
        }
        
        
        
    }
    
    

    @Override
    public int getItemCount() {
        return calendars.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView startTime;
        final TextView endTime;
        final ImageView image;
        final TextView title;
        final View titleBar;
        final ImageButton feedbackButton;
        
        final View itemView;
        
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            startTime = (TextView) itemView.findViewById(R.id.start_time);
            endTime = (TextView) itemView.findViewById(R.id.end_time);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            titleBar = itemView.findViewById(R.id.title_bar);
            feedbackButton = (ImageButton) itemView.findViewById(R.id.give_feedback_button);
        }
        
        
        
    }
}
