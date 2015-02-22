package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
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
import org.jboss.aerogear.devnexus2015.util.AddSessionClickListener;
import org.jboss.aerogear.devnexus2015.util.ColorUtils;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by summers on 2/15/15.
 */
public class MyScheduleViewAdapter extends RecyclerView.Adapter<MyScheduleViewAdapter.ViewHolder>  {
    private final ArrayList<UserCalendar> calendars;
    private final Context context;
    private final SessionClickListener sessionClickListener;
    private final AddSessionClickListener addSessionClickListener;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    
    public MyScheduleViewAdapter(ArrayList<UserCalendar> userCalendars, Context context, SessionClickListener sessionClickListener, AddSessionClickListener addSessionClickListener) {
        this.calendars = userCalendars;
        this.sessionClickListener = sessionClickListener;
        this.addSessionClickListener = addSessionClickListener;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserCalendar userCalendarItem = calendars.get(position);
        
        holder.startTime.setText(TIME_FORMAT.format(userCalendarItem.fromTime));
        holder.endTime.setText(TIME_FORMAT.format(userCalendarItem.getToTime()));
        
        if (userCalendarItem.item == null &&!userCalendarItem.fixed) {
            holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_white)));
            holder.title.setText( "Select a Session ");
            holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
            holder.titleBar.setBackgroundColor(context.getResources().getColor(R.color.dn_white));
            holder.feedbackButton.setVisibility(View.GONE);
            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSessionPicker(userCalendarItem);        
                }
            });
            
        } else {
            ScheduleItem item = userCalendarItem.item;
            if (item!= null) {
                int color = context.getResources().getColor(TrackRoomUtil.forTrack(item.presentation.track.name));
                holder.title.setText(item.presentation.title);
                holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                Picasso.with(context).load("https://devnexus.com/s/speakers/"+item.presentation.speakers.get(0).id+".jpg").into(holder.image);
                holder.titleBar.setBackgroundColor(color);
                holder.feedbackButton.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSessionInfo(userCalendarItem);
                    }
                });


                holder.title.setTextColor(ColorUtils.getTextColor(context, color));
                
                
                
            } else {
                holder.title.setText(userCalendarItem.fixedTitle);
                holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                holder.titleBar.setBackgroundColor(context.getResources().getColor(R.color.dn_white));
                holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_white)));
                holder.feedbackButton.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        return;//do nothing
                    }
                });
            }
            
        }
        
        
        
    }

    private void showSessionInfo(UserCalendar userCalendarItem) {
        sessionClickListener.loadSession(userCalendarItem.item.presentation);
    }

    private void showSessionPicker(UserCalendar userCalendarItem) {
        addSessionClickListener.showPicker(userCalendarItem);
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
