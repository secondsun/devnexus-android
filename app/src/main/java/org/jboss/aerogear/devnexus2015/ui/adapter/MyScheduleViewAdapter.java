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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.Room;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.UserCalendarContract;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.AddSessionClickListener;
import org.jboss.aerogear.devnexus2015.util.ColorUtils;
import org.jboss.aerogear.devnexus2015.util.Optional;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by summers on 2/15/15.
 */
public class MyScheduleViewAdapter extends RecyclerView.Adapter<MyScheduleViewAdapter.ViewHolder> {
    //private final ArrayList<UserCalendar> calendars;
    private final ArrayList<TimeOrPresentation> timeOrPresentations;
    private final Context context;
    private final SessionClickListener sessionClickListener;
    private final AddSessionClickListener addSessionClickListener;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    private static final int TIME_TYPE = 0;
    private static final int SESSION_TYPE = 1;

    public MyScheduleViewAdapter(ArrayList<UserCalendar> userCalendars, Context context, SessionClickListener sessionClickListener, AddSessionClickListener addSessionClickListener) {
        //this.calendars = userCalendars;
        this.timeOrPresentations = makeTimeOrPresentationItems(userCalendars);
        this.sessionClickListener = sessionClickListener;
        this.addSessionClickListener = addSessionClickListener;
        this.context = context.getApplicationContext();
    }

    private ArrayList<TimeOrPresentation> makeTimeOrPresentationItems(ArrayList<UserCalendar> userCalendars) {
        ArrayList<TimeOrPresentation> toReturn = new ArrayList<>(userCalendars.size() * 3);//Headers will always be number of options.  All options have at least one optional.  Assume an average of two in the worse case scenarion.
        for (UserCalendar calendarItem : userCalendars) {
            toReturn.add(new TimeOrPresentation(calendarItem));
            if (calendarItem.fixed) {
                ScheduleItem fixedItem = new ScheduleItem();
                fixedItem.title = calendarItem.fixedTitle;
                toReturn.add(new TimeOrPresentation(calendarItem, fixedItem));
            } else {
                if (calendarItem.items != null) {
                    for (ScheduleItem scheduleItem : calendarItem.items) {
                        toReturn.add(new TimeOrPresentation(calendarItem, scheduleItem));
                    }
                }
                toReturn.add(new TimeOrPresentation(calendarItem, (ScheduleItem) null));//add empty item to prompt the select box}
            }

        }
        return toReturn;
    }

    public int getDateItemIndex(Date date) {
        int index = 0;

        for (TimeOrPresentation item : timeOrPresentations) {
            if (item.scheduleItem == null) {
                if (item.userCalendar.fromTime.after(date)) {
                    return index;
                }
            }
            index++;
        }
        return 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder vh;
        switch (viewType) {
            case TIME_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.usercalendar_layout_time_header, parent, false);

                vh = new ViewHolder(v);
                break;
            case SESSION_TYPE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.usercalendar_layout_calendar_item, parent, false);

                vh = new ViewHolder(v);
                break;
            default:
                throw new IllegalArgumentException(viewType + " is not a supprted viewType");
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TimeOrPresentation timeOrPresentation = this.timeOrPresentations.get(position);
        final UserCalendar userCalendarItem = timeOrPresentation.userCalendar;

        switch (getItemViewType(position)) {
            case TIME_TYPE: {

                holder.startTime.setText(TIME_FORMAT.format(userCalendarItem.fromTime));
                holder.endTime.setText(TIME_FORMAT.format(userCalendarItem.getToTime()));
            }
            break;
            case SESSION_TYPE: {
                final ScheduleItem scheduleItem = timeOrPresentation.scheduleItem.getItem();
                if (userCalendarItem.fixed) {
                    holder.title.setText(userCalendarItem.fixedTitle + " - " + userCalendarItem.room);
                    holder.title.setTextColor(context.getResources().getColor(R.color.dn_white));
                    if (userCalendarItem.color != null) {
                        holder.box.setBackgroundColor(Color.parseColor(userCalendarItem.color));
                    } else {
                        if (userCalendarItem.items.size() > 0) {

                        } else {
                            holder.box.setBackgroundColor(context.getResources().getColor(R.color.dn_default));
                        }

                    }

                    holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_light_gray)));
                    holder.removeButton.setVisibility(View.GONE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userCalendarItem.items != null && userCalendarItem.items.size() > 0 ) {
                                showSessionInfo(userCalendarItem.items.iterator().next());
                            }
                        }
                    });
                } else if (scheduleItem == null) {
                    holder.image.setImageDrawable(new ColorDrawable(context.getResources().getColor(R.color.dn_white)));
                    holder.title.setText("Select a Session ");
                    holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                    holder.box.setBackgroundColor(context.getResources().getColor(R.color.dn_white));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSessionPicker(userCalendarItem);
                        }
                    });
                } else {
                    int color = context.getResources().getColor(TrackRoomUtil.colorForTrack(scheduleItem.presentation.track.name));
                    if (scheduleItem.room == null) {
                        scheduleItem.room = new Room();
                    }
                    holder.title.setText(scheduleItem.presentation.title + " - " + scheduleItem.room.name);
                    holder.title.setTextColor(context.getResources().getColor(R.color.dn_black));
                    holder.box.setBackgroundColor(color);
                    if (!userCalendarItem.fixed) {
                        holder.removeButton.setVisibility(View.VISIBLE);

                        holder.removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userCalendarItem.items.remove(scheduleItem);
                                context.getContentResolver().update(UserCalendarContract.URI, UserCalendarContract.valueize(userCalendarItem, true), UserCalendarContract.ID, new String[]{userCalendarItem.getId() + ""});
                            }
                        });
                    } else {
                        holder.removeButton.setVisibility(View.GONE);
                    }
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSessionInfo(scheduleItem);
                        }
                    });

                    holder.image.setBackgroundColor(color);
                    holder.image.setImageResource(TrackRoomUtil.iconForTrack(scheduleItem.presentation.track.name));

                    holder.title.setTextColor(ColorUtils.getTextColor(context, color));


                }
            }
            break;
            default:
                throw new RuntimeException("Unknown type " + getItemViewType(position));
        }


    }

    private void showSessionInfo(ScheduleItem item) {
        sessionClickListener.loadSession(item.presentation);
    }

    private void showSessionPicker(UserCalendar userCalendarItem) {
        addSessionClickListener.showPicker(userCalendarItem);
    }


    @Override
    public int getItemViewType(int position) {
        return timeOrPresentations.get(position).scheduleItem != null ? SESSION_TYPE : TIME_TYPE;
    }


    @Override
    public int getItemCount() {
        return timeOrPresentations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView startTime;
        final TextView endTime;
        final ImageView image;
        final TextView title;
        final RelativeLayout box;

        final ImageButton removeButton;

        final View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            startTime = (TextView) itemView.findViewById(R.id.start_time);
            endTime = (TextView) itemView.findViewById(R.id.end_time);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            box = (RelativeLayout) itemView.findViewById(R.id.box);

            removeButton = (ImageButton) itemView.findViewById(R.id.remove_session_from_schedule);
        }

    }

    private static final class TimeOrPresentation {
        UserCalendar userCalendar;
        Optional<ScheduleItem> scheduleItem;//horrible use of optional.

        TimeOrPresentation(UserCalendar userCalendar) {
            this.userCalendar = userCalendar;
            this.scheduleItem = null;
        }

        TimeOrPresentation(UserCalendar userCalendarItem, ScheduleItem scheduleItem) {
            this.userCalendar = userCalendarItem;
            this.scheduleItem = Optional.newOption(scheduleItem);
        }

    }

}

