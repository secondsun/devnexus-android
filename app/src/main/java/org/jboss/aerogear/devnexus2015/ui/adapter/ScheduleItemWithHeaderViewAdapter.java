package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.ScheduleItem;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.fragment.PresentationExplorerFragment;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class ScheduleItemWithHeaderViewAdapter extends RecyclerView.Adapter<ScheduleItemWithHeaderViewAdapter.ViewHolder>  {

    private final List<ItemOrHeader> items;
    private final Context mContext;
    private final boolean hideImages;
    private SessionClickListener clickListener;


    public ScheduleItemWithHeaderViewAdapter(List<ScheduleItem> items, Context mContext) {
        this.items = makeList(items);
        this.mContext = mContext;
        this.hideImages = false;
        clickListener = null;
    }

    public ScheduleItemWithHeaderViewAdapter(List<ScheduleItem> items, Context mContext, boolean hideImages) {
        this.items = makeList(items);
        this.mContext = mContext;
        this.hideImages = hideImages;
        clickListener = null;
    }

    public ScheduleItemWithHeaderViewAdapter(List<ScheduleItem> items, Context mContext, boolean hideImages, SessionClickListener listener) {
        this.items = makeList(items);
        this.mContext = mContext;
        this.hideImages = hideImages;
        clickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType()== ItemOrHeader.TYPE.HEADER?0:1;
    }
    
    

    private List<ItemOrHeader> makeList(List<ScheduleItem> items) {
        SimpleDateFormat headerFormat = new SimpleDateFormat("MMM-dd hh:mm a");
        ArrayList<ItemOrHeader> toReturn = new ArrayList<>(items.size() * 2);
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

                if (hideImages) {
                    photo.setVisibility(View.GONE);
                }

                if (item.presentation != null) {
                    final int trackColor = mContext.getResources().getColor(TrackRoomUtil.forTrack(item.presentation.track.name));
                    if (!hideImages) {
                        photo.setBackgroundColor(trackColor);
                        Log.d("Presentation Image", "https://devnexus.com/s/speakers/" + item.presentation.speakers.get(0).id + ".jpg");
                        Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.presentation.speakers.get(0).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().into(photo);
                    }
                    TextView infoText = ((TextView) viewHolder.sessionView.findViewById(R.id.info_text));
                    infoText.setText(item.presentation.title);
                    infoText.setBackgroundColor(trackColor);
                    infoText.setTextColor( mContext.getResources().getColor(R.color.dn_white));

                    viewHolder.sessionView.setOnClickListener(new View.OnClickListener() {
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
                return items.get(position).getType()== ItemOrHeader.TYPE.ITEM?1:2;
            }
        };
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setClickListener(PresentationExplorerFragment clickListener) {
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
        
        enum TYPE {HEADER, ITEM};
        
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
        
    }

}
