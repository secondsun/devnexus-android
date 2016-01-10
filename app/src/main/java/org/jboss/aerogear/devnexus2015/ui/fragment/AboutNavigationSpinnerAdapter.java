package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 1/10/16.
 */
public class AboutNavigationSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context context;

    public AboutNavigationSpinnerAdapter(Activity activity) {
        this.context = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        switch (position) {
            case 0:
                return "Sponsors";
            case 1:
                return "Open Source";
            default:
                return "";
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.schedule_spinner_topic_layout_item, null);
            convertView.findViewById(R.id.header_label).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.color).setVisibility(View.INVISIBLE);
        }

        ((TextView)convertView.findViewById(R.id.label)).setText(getItem(position).toString());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.schedule_spinner_topic_layout, null);
            convertView.findViewById(R.id.header_label).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.color).setVisibility(View.INVISIBLE);
        }

        ((TextView)convertView.findViewById(R.id.label)).setText(getItem(position).toString());

        return convertView;
    }
}
