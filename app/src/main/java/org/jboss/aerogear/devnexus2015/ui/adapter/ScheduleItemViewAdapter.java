package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.graphics.LightingColorFilter;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class ScheduleItemViewAdapter  extends RecyclerView.Adapter<ScheduleItemViewAdapter.ViewHolder>  {

    private final List<ScheduleItem> items;
    private final Context mContext;

    public ScheduleItemViewAdapter(List<ScheduleItem> items, Context mContext) {
        this.items = new ArrayList<>(items);
        this.mContext = mContext;
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
        ScheduleItem item = items.get(i);

        ImageView photo = (ImageView) viewHolder.sessionView.findViewById(R.id.photo);

        if (item.presentation != null) {
            final int trackColor = mContext.getResources().getColor(TrackRoomUtil.forTrack(item.presentation.track.name));
            photo.setBackgroundColor(trackColor);
            photo.setColorFilter(new LightingColorFilter(trackColor, 1));

            Log.d("Presentation Image", "https://devnexus.com/s/speakers/" + item.presentation.speakers.get(0).id + ".jpg");
            Picasso.with(mContext).load("https://devnexus.com/s/speakers/"+item.presentation.speakers.get(0).id+".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().into(photo);
            ((TextView) viewHolder.sessionView.findViewById(R.id.info_text)).setText(item.presentation.title);

            viewHolder.sessionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //clickListener.loadSession(item);
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

    public static class ViewHolder  extends RecyclerView.ViewHolder {

        public View sessionView;
        public ViewHolder(View itemView) {
            super(itemView);
            sessionView = itemView;
        }
    }


}
