package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.jboss.aerogear.devnexus2015.model.BadgeContact;

import java.util.List;

/**
 * Created by summers on 1/7/16.
 */
public class BadgeContactViewAdapter extends RecyclerView.Adapter<BadgeContactViewAdapter.BadgeContactView> {

    private final List<BadgeContact> conacts;

    public BadgeContactViewAdapter(List<BadgeContact> badgeContacts) {
        this.conacts = badgeContacts;
    }


    @Override
    public BadgeContactView onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BadgeContactView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class BadgeContactView extends RecyclerView.ViewHolder {

        public BadgeContactView(View itemView) {
            super(itemView);
        }
    }

}
