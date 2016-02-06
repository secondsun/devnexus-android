package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.ScheduleItem;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class layous out presentations separated by userCalendar.  It assumes the presentations are in a gridlayout.
 */
public class ScheduleItemWithHeaderViewAdapter extends RecyclerView.Adapter<ScheduleItemWithHeaderViewAdapter.ViewHolder>  {

    private final List<ItemOrHeader> items;
    private final Context mContext;
    private final boolean hideImages;
    private final int columnCount;
    private SessionClickListener clickListener;


    public ScheduleItemWithHeaderViewAdapter(List<ScheduleItem> items, Context mContext, int columnCount) {
        this.items = makeList(items);
        this.mContext = mContext;
        this.hideImages = false;
        clickListener = null;
        this.columnCount = columnCount;
    }


    public ScheduleItemWithHeaderViewAdapter(List<ScheduleItem> items, Context mContext, int columnCount, boolean hideImages, SessionClickListener listener) {
        this.items = makeList(items);
        this.mContext = mContext;
        this.hideImages = hideImages;
        this.columnCount = columnCount;
        clickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType()== ItemOrHeader.TYPE.HEADER?0:1;
    }


    /**
     * Creates a in order list of items that are headers or items
     *
     * @param items
     * @return
     */
    private List<ItemOrHeader> makeList(List<ScheduleItem> items) {
        SimpleDateFormat headerFormat = new SimpleDateFormat("MMM-dd hh:mm a");
        ArrayList<ItemOrHeader> toReturn = new ArrayList<>(items.size() * 3);
        ScheduleItem previousItem = null;
        for (ScheduleItem item : items) {
            if (previousItem == null || !item.fromTime.equals(previousItem.fromTime)) {
                ItemOrHeader header = new ItemOrHeader(headerFormat.format(item.fromTime), null);
                toReturn.add(header);
            }
            toReturn.add(new ItemOrHeader(null, item));

            previousItem = item;
        }
        return toReturn;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final ItemOrHeader itemOrHeader = items.get(i);
        View v = null;
        switch (itemOrHeader.getType()){
            case HEADER:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.schedule_time_layout, viewGroup, false);
                break;
            case ITEM:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.schedule_item_layout, viewGroup, false); 
                break;
        }
         

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    public int getDateIndex(Date date) {
        int index = 0;

        for (ItemOrHeader item : items) {
            if (item.item != null) {
                ScheduleItem scheduleItem = item.item;
                if (scheduleItem.fromTime.after(date)) {
                    return index;
                }
            }
            index++;
        }

        return 0;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final ItemOrHeader itemOrHeader = items.get(i);
        
        switch (itemOrHeader.getType()){
            case HEADER:
                final String header = itemOrHeader.get();
                ((TextView)viewHolder.sessionView.findViewById(R.id.date)).setText(header);
                break;
            case ITEM:
                final ScheduleItem item = itemOrHeader.get();
                ImageView photo = (ImageView) viewHolder.sessionView.findViewById(R.id.photo);
                ImageView photo2 = (ImageView) viewHolder.sessionView.findViewById(R.id.photo_2);
                ImageView photo3 = (ImageView) viewHolder.sessionView.findViewById(R.id.photo_3);
                ImageView icon = (ImageView) viewHolder.sessionView.findViewById(R.id.icon);

                photo2.setVisibility(View.GONE);
                photo3.setVisibility(View.GONE);

                if (hideImages) {
                    photo.setVisibility(View.GONE);
                }

                if (item.presentation != null) {
                    final int trackColor = mContext.getResources().getColor(TrackRoomUtil.colorForTrack(item.presentation.track.name));
                    final int iconResource= TrackRoomUtil.iconForTrack(item.presentation.track.name);
                    if (!hideImages) {
                        Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.presentation.speakers.get(0).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo);
                        if (item.presentation.speakers.size() == 2 ) {
                            photo2.setVisibility(View.VISIBLE);
                            photo3.setVisibility(View.GONE);
                            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.presentation.speakers.get(1).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo2);
                        } else if (item.presentation.speakers.size() == 3 ) {
                            photo2.setVisibility(View.VISIBLE);
                            photo3.setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.presentation.speakers.get(1).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo2);
                            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.presentation.speakers.get(2).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo3);
                        }
                    }
                    TextView infoText = ((TextView) viewHolder.sessionView.findViewById(R.id.info_text));
                    infoText.setText(item.presentation.title);
                    icon.setBackgroundColor(trackColor);
                    icon.setImageResource(iconResource);

                    viewHolder.sessionView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (clickListener != null) {
                                clickListener.loadSession(item.presentation);
                            }
                        }
                    });
                } else {
                    ((TextView) viewHolder.sessionView.findViewById(R.id.info_text)).setText(item.title);
                }
                break;
        }
        
        

    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return items.get(position).getType()== ItemOrHeader.TYPE.ITEM?1:columnCount;
            }
        };
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setClickListener(SessionClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {
        public View sessionView;
        public ViewHolder(View itemView) {
            super(itemView);
            sessionView = itemView;
        }
    }

    private static class ItemOrHeader {
        
        final String header;
        final ScheduleItem item;
        private ItemOrHeader(String header, ScheduleItem item) {
            this.header = header;
            this.item = item;
        }

        TYPE getType() {
            return item == null?TYPE.HEADER:TYPE.ITEM;
        }

        <T> T  get(){
            return (T) (item == null?header:item);
        }

        enum TYPE {HEADER, ITEM}
        
    }

}
