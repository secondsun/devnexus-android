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
import org.devnexus.vo.Presentation;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.SessionClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class PresentationViewAdapter extends RecyclerView.Adapter<PresentationViewAdapter.ViewHolder> {

    private static final String TAG = PresentationViewAdapter.class.getSimpleName();
    private final List<Presentation> items;
    private final Context mContext;
    private final SessionClickListener clickListener;

    public PresentationViewAdapter(List<Presentation> items, Context context, SessionClickListener clickListener) {
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
        final Presentation item = items.get(i);
        if (item == null) {
            Log.d("AdapterFailing", this.toString());
            Log.e(TAG, " No presentation at index i " + i);
        }
        ImageView photo = (ImageView) viewHolder.presentationView.findViewById(R.id.photo);
        ImageView photo2 = (ImageView) viewHolder.presentationView.findViewById(R.id.photo_2);
        ImageView photo3 = (ImageView) viewHolder.presentationView.findViewById(R.id.photo_3);
        ImageView icon = (ImageView) viewHolder.presentationView.findViewById(R.id.icon);

        int trackColor = mContext.getResources().getColor(R.color.WORKSHOP);

        if (item.track != null) {
            trackColor = mContext.getResources().getColor(TrackRoomUtil.colorForTrack(item.track.name));
            icon.setImageResource(TrackRoomUtil.iconForTrack(item.track.name));
        }

        icon.setBackgroundColor(trackColor);

        TextView infoText = ((TextView) viewHolder.presentationView.findViewById(R.id.info_text));
        infoText.setText(item.title);

        Log.d("Presentation Image", "https://devnexus.com/s/speakers/" + item.speakers.get(0).id + ".jpg");

        ((TextView) viewHolder.presentationView.findViewById(R.id.info_text)).setText(item.title);

        photo2.setVisibility(View.GONE);
        photo3.setVisibility(View.GONE);

        final int iconResource = TrackRoomUtil.iconForTrack(item.track.name);

        Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.speakers.get(0).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo);
        if (item.speakers.size() == 2) {
            photo2.setVisibility(View.VISIBLE);
            photo3.setVisibility(View.GONE);
            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.speakers.get(1).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo2);
        } else if (item.speakers.size() == 3) {
            photo2.setVisibility(View.VISIBLE);
            photo3.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.speakers.get(1).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo2);
            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + item.speakers.get(2).id + ".jpg").placeholder(new ColorDrawable(trackColor)).fit().centerCrop().noFade().into(photo3);
        }

        infoText.setText(item.title);
        icon.setBackgroundColor(trackColor);
        icon.setImageResource(iconResource);

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View presentationView;

        public ViewHolder(View itemView) {
            super(itemView);
            presentationView = itemView;
        }
    }

}
