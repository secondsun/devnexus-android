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
