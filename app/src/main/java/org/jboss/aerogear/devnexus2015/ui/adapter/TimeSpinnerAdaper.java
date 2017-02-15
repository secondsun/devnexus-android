package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by summers on 1/3/15.
 */
public class TimeSpinnerAdaper extends BaseAdapter implements SpinnerAdapter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final int TOPIC_VIEW = 0x100;
    private static final int HEADER_VIEW = 0x200;
    private static final int SPACER_VIEW = 0x300;
    private final Context context;

    public TimeSpinnerAdaper(Context context) {
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ITEMS item = (ITEMS) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.schedule_spinner_topic_layout_item, null);
        }
        switch (getActualItemViewType(position)) {
            case SPACER_VIEW:
                convertView.findViewById(R.id.header_label).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.label).setVisibility(View.GONE);
                convertView.findViewById(R.id.spacer).setVisibility(View.VISIBLE);
                return convertView;

            case TOPIC_VIEW:
                convertView.findViewById(R.id.header_label).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.label).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spacer).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.dn_blue));
                ((TextView) convertView.findViewById(R.id.label)).setText((item.getTitleString()));
                return convertView;

            case HEADER_VIEW:

                convertView.findViewById(R.id.header_label).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.label).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.spacer).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.header_label)).setTextColor(context.getResources().getColor(R.color.dn_light_gray));
                ((TextView) convertView.findViewById(R.id.header_label)).setText((item.getTitleString()));
                return convertView;
            default:
                throw new IllegalStateException("Illegal view type");
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        //do nothing
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        //do nothing
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return ((ITEMS) getItem(position)).isClickable();
    }

    @Override
    public int getCount() {
        return ITEMS.values().length;
    }

    @Override
    public Object getItem(int position) {
        return ITEMS.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ITEMS item = (ITEMS) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.schedule_spinner_topic_layout, null);
        }
        switch (getActualItemViewType(position)) {
            case TOPIC_VIEW:
                convertView.findViewById(R.id.header_label).setVisibility(View.INVISIBLE);
                convertView.findViewById(R.id.label).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spacer).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.dn_blue));
                ((TextView) convertView.findViewById(R.id.label)).setText((item.getTitleString()));
                convertView.setTag(item.getFromTime());
                return convertView;

            default:
                return convertView;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return SPACER_VIEW;
    }


    private int getActualItemViewType(int position) {
        ITEMS item = ITEMS.values()[position];
        if (item.isSpacer) {
            return SPACER_VIEW;
        } else if (item.isTitle) {
            return HEADER_VIEW;
        } else {
            return TOPIC_VIEW;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    public enum ITEMS {


        ALL_TIME("Full Schedule", 3600, toDate("22/2/2017 00:00"), true, false, false, R.color.dn_white),
        SPACER_0("", 3600, toDate("22/2/2017 00:00"), false, false, true, R.color.dn_white),
        WEDNESDAY("Wednesday", 720, toDate("22/2/2017 08:00"), true, true, false, R.color.dn_white),
        WED_9_AM("9:00 AM", 510, toDate("22/2/2017 09:00"), true, false, false, R.color.dn_white),

        SPACER_1("", 3600, toDate("22/2/2017 00:00"), false, false, true, R.color.dn_white),
        THURSDAY("Thursday", 720, toDate("23/2/2017 08:00"), true, false, false, R.color.dn_white),
        THURS_9_AM("9:15 AM", 90, toDate("23/2/2017 09:15"), true, false, false, R.color.dn_white),
        THURS_10_30_AM("10:30 AM", 90, toDate("23/2/2017 10:30"), true, false, false, R.color.dn_white),
        THURS_1_PM("1:00 PM", 90, toDate("23/2/2017 13:00"), true, false, false, R.color.dn_white),
        THURS_2_30_PM("2:30 PM", 90, toDate("23/2/2017 14:30"), true, false, false, R.color.dn_white),
        THURS_4_PM("4:00 PM", 90, toDate("23/2/2017 16:00"), true, false, false, R.color.dn_white),
        THURS_5_30_PM("5:300 PM", 90, toDate("23/2/2017 17:30"), true, false, false, R.color.dn_white),

        SPACER_2("", 3600, toDate("24/2/2017 00:00"), false, false, true, R.color.dn_white),
        FRIDAY("Friday", 720, toDate("24/2/2017 08:00"), true, false, false, R.color.dn_white),
        FRI_9_AM("9:15 AM", 90, toDate("24/2/2017 09:15"), true, false, false, R.color.dn_white),
        FRI_10_30_AM("10:30 AM", 90, toDate("24/2/2017 10:30"), true, false, false, R.color.dn_white),
        FRI_1_PM("1:00 PM", 90, toDate("24/2/2017 13:00"), true, false, false, R.color.dn_white),
        FRI_2_30_PM("2:30 PM", 90, toDate("24/2/2017 14:30"), true, false, false, R.color.dn_white),
        FRI_4_PM("4:00 PM", 90, toDate("24/2/2017 16:00"), true, false, false, R.color.dn_white);

        private static Date toDate(String s) {
            try {
                return sdf.parse(s);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        private final String titleString;
        private final boolean isClickable;
        private final boolean isTitle;
        private final boolean isSpacer;
        private final int rightDrawable;
        private Date fromTime;
        private int duration;

        private ITEMS(String stringResource, int duration, Date fromTime, boolean isClickable, boolean isTitle, boolean isSpacer, int rightDrawable) {
            titleString = stringResource;
            this.isClickable = isClickable;
            this.isTitle = isTitle;
            this.isSpacer = isSpacer;
            this.rightDrawable = rightDrawable;
            this.duration = duration;
            this.fromTime = fromTime;
        }

        public String getTitleString() {
            return titleString;
        }

        public boolean isClickable() {
            return isClickable;
        }

        public boolean isTitle() {
            return isTitle;
        }

        public boolean isSpacer() {
            return isSpacer;
        }

        public int getRightDrawable() {
            return rightDrawable;
        }

        public Date getFromTime() {
            return fromTime;
        }
    }

    ;


}
