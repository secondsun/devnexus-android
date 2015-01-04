package org.jboss.aerogear.devnexus2015.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;

/**
 * Created by summers on 1/3/15.
 */
public class SessionSpinnerAdaper implements SpinnerAdapter {

    private static final int TOPIC_VIEW = 0x100;
    private static final int HEADER_VIEW = 0x200;
    private static final int SPACER_VIEW = 0x300;
    private final Context context;

    public SessionSpinnerAdaper(Context context) {
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
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
            case SPACER_VIEW:

                convertView.findViewById(R.id.label).setVisibility(View.GONE);
                convertView.findViewById(R.id.color).setVisibility(View.GONE);
                convertView.findViewById(R.id.spacer).setVisibility(View.VISIBLE);
                return convertView;

            case TOPIC_VIEW:
                convertView.findViewById(R.id.label).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.color).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spacer).setVisibility(View.GONE);
                ((TextView) convertView.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.dn_blue));
                ((TextView) convertView.findViewById(R.id.label)).setText(context.getText(item.getTitleStringResource()));
                convertView.findViewById(R.id.color).setBackgroundResource(item.getRightDrawable());

                return convertView;

            case HEADER_VIEW:

                convertView.findViewById(R.id.label).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.color).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spacer).setVisibility(View.GONE);
                convertView.findViewById(R.id.color).setBackgroundResource(item.getRightDrawable());
                ((TextView) convertView.findViewById(R.id.label)).setTextColor(context.getResources().getColor(R.color.dn_light_gray));
                ((TextView) convertView.findViewById(R.id.label)).setText(context.getText(item.getTitleStringResource()));
                return convertView;
            default:
                throw new IllegalStateException("Illegal view type");
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
        ALL_EVENTS(R.string.all_events, true, false, false, R.color.dn_white),
        SPACER_0(0, false, false, true, R.color.dn_white),
        EVENT_TYPES(R.string.event_type, false, true, false, R.color.dn_white),
        WORKSHOPS(R.string.workshops, true, false, false, R.color.dn_white),
        BREAKOUTS(R.string.breakouts, true, false, false, R.color.dn_white),
        BREAKS_AND_SOCIAL(R.string.breaks_and_social, true, false, false, R.color.dn_white),
        SPACER_1(0, false, false, true, R.color.dn_white),
        SESSION_TOPIC(R.string.topics, false, true, false, R.color.dn_white),
        AGILE(R.string.topic_agile, true, false, false, R.color.Agile),
        CLOUD_DEVOPTS(R.string.topic_cloud_and_devopts, true, false, false, R.color.Cloud_DevOps),
        DATA_INTEGRATION_IOT(R.string.topic_data_integration_iot, true, false, false, R.color.Data_Integration_IoT),
        FUNCTIONAL_PROGRAMMING(R.string.topic_functional_programming, true, false, false, R.color.Functional_Programming),
        HTML_5(R.string.topic_html5, true, false, false, R.color.HTML5),
        JAVA(R.string.topic_java, true, false, false, R.color.Java),
        JAVASCRIPT(R.string.topic_javascript, true, false, false, R.color.JavaScript),
        JVM_LANGUAGES(R.string.topic_jvm_languages, true, false, false, R.color.JVM_Languages_Debugging),
        KEYNOTES(R.string.keynote, true, false, false, R.color.Keynotes),
        MICROSERVICES_SECURITY(R.string.topic_microservices_and_security, true, false, false, R.color.Microservices_Security),
        MOBILE(R.string.topic_mobile, true, false, false, R.color.Mobile),
        USER_EXPERIENCE_AND_TOOLS(R.string.topic_user_experience_plus_tools, true, false, false, R.color.User_Experience_Tools),
        WEB(R.string.topic_web, true, false, false, R.color.Web),
        WORKSHOP(R.string.workshops, true, false, false, R.color.Workshop),;

        private final int titleStringResource;
        private final boolean isClickable;
        private final boolean isTitle;
        private final boolean isSpacer;
        private final int rightDrawable;

        private ITEMS(int stringResource, boolean isClickable, boolean isTitle, boolean isSpacer, int rightDrawable) {
            titleStringResource = stringResource;
            this.isClickable = isClickable;
            this.isTitle = isTitle;
            this.isSpacer = isSpacer;
            this.rightDrawable = rightDrawable;
        }

        public int getTitleStringResource() {
            return titleStringResource;
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
    }

    ;


}