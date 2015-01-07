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
import org.devnexus.vo.Podcast;
import org.devnexus.vo.Presentation;
import org.jboss.aerogear.devnexus2015.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class PodcastViewAdapter extends RecyclerView.Adapter<PodcastViewAdapter.ViewHolder>  {

    private final List<Podcast> items;
    private final Context mContext;
    private final SessionClickListener clickListener;

    public PodcastViewAdapter(List<Podcast> items, Context context, SessionClickListener clickListener) {
        this.items = new ArrayList<>(items);
        this.mContext = context;
        this.clickListener = clickListener;
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
        final Podcast item = items.get(i);
        ImageView photo = (ImageView) viewHolder.presentationView.findViewById(R.id.photo);
        int trackColor = mContext.getResources().getColor(R.color.dn_default);
        
        if (item.track != null) {
            trackColor = mContext.getResources().getColor(TrackRoomUtil.forTrack(item.track));
        }
        photo.setBackgroundColor(trackColor);

        if (trackColor != mContext.getResources().getColor(R.color.dn_default)) {
            photo.setColorFilter(new LightingColorFilter(trackColor, 1));
        }
        
        ((TextView) viewHolder.presentationView.findViewById(R.id.info_text)).setText(item.title);

        viewHolder.presentationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.loadSession(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {

        public View presentationView;
        public ViewHolder(View itemView) {
            super(itemView);
            presentationView = itemView;
        }
    }

    public interface SessionClickListener {

        public void loadSession(Podcast presentation);

    }


}
