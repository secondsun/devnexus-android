package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.PresentationTag;
import org.jboss.aerogear.devnexus2015.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by summers on 1/5/15.
 */
public class TagAdapter implements ListAdapter {

    private final List<PresentationTag> tags;
    private final Context context;

    public TagAdapter(Presentation presentation, Context context) {
        this.context = context;
        tags = new ArrayList<PresentationTag>(presentation.presentationTags);
        Collections.sort(tags);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        //nothing
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        //nothing
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tags.get(position).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tag_item_layout, parent, false);

        }
        PresentationTag tag = (PresentationTag) getItem(position);
        int color = TrackRoomUtil.colorForTrack(tag.getName());
        View colorView = convertView.findViewById(R.id.color);
        TextView tagName = (TextView) convertView.findViewById(R.id.tag_name);
        if (color == R.color.dn_default) {
            colorView.setVisibility(View.GONE);
        } else {
            colorView.setVisibility(View.VISIBLE);
            colorView.setBackgroundColor(context.getResources().getColor(color));
        }

        tagName.setText(tag.name);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return tags.isEmpty();
    }

}

