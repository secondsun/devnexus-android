package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.Podcast;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.PodcastClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class PodcastViewAdapter extends RecyclerView.Adapter<PodcastViewAdapter.ViewHolder>  {

    private final List<Podcast> items;
    private final Context mContext;
    private final PodcastClickListener clickListener;

    public PodcastViewAdapter(List<Podcast> items, Context context, PodcastClickListener clickListener) {
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
        photo.setVisibility(View.GONE);

        int trackColor = mContext.getResources().getColor(R.color.dn_default);

        if (item.track != null) {
            trackColor = mContext.getResources().getColor(TrackRoomUtil.forTrack(item.track));
        }


        TextView infoText = ((TextView) viewHolder.presentationView.findViewById(R.id.info_text));
        infoText.setText(item.title);
        infoText.setBackgroundColor(trackColor);
        infoText.setTextColor( mContext.getResources().getColor(R.color.dn_white));

        viewHolder.presentationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.playPodcast(item);
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


}
