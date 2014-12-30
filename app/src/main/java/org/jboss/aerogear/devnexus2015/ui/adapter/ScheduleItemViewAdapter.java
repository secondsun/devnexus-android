package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.devnexus.vo.Schedule;
import org.devnexus.vo.ScheduleItem;
import org.jboss.aerogear.devnexus2015.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 12/28/14.
 */
public class ScheduleItemViewAdapter  extends RecyclerView.Adapter<ScheduleItemViewAdapter.ViewHolder>  {

    private final List<ScheduleItem> items;

    public ScheduleItemViewAdapter(List<ScheduleItem> items) {
        this.items = new ArrayList<>(items);
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
        ((TextView)viewHolder.sessionView.findViewById(R.id.info_text)).setText(item.title);

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
