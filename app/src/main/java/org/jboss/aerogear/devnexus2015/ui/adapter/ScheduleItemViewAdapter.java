package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class ScheduleItemViewAdapter  extends RecyclerView.Adapter<ScheduleItemViewAdapter.ViewHolder>  {

    private final List<ScheduleItem> items;
    private final Context mContext;
    private final boolean hideImages;
    private SessionClickListener clickListener;


    public ScheduleItemViewAdapter(List<ScheduleItem> items, Context mContext) {
        this.items = new ArrayList<>(items);
        this.mContext = mContext;
        this.hideImages = false;
        clickListener = null;
    }

    public ScheduleItemViewAdapter(List<ScheduleItem> items, Context mContext, boolean hideImages) {
        this.items = new ArrayList<>(items);
        this.mContext = mContext;
        this.hideImages = hideImages;
        clickListener = null;
    }

    public ScheduleItemViewAdapter(List<ScheduleItem> items, Context mContext, boolean hideImages, SessionClickListener listener) {
        this.items = new ArrayList<>(items);
        this.mContext = mContext;
        this.hideImages = hideImages;
        clickListener = listener;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.schedule_item_layout, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final ScheduleItem item = items.get(i);

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


}
