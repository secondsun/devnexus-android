package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.DrawerItem;

import java.util.List;

public class DrawerListAdapter extends BaseAdapter {

    private final Context context;
    private final List<DrawerItem> items;

    public DrawerListAdapter(Context context, List<DrawerItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.drawer_item_layout, null);

        DrawerItem item = items.get(position);

        ImageView image = (ImageView) view.findViewById(R.id.icon);
        image.setImageResource(item.getIconResId());

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(item.getText());

        return view;
    }
}
