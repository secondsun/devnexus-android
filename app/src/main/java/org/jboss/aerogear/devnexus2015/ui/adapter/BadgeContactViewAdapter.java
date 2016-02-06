package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.devnexus.vo.BadgeContact;
import org.jboss.aerogear.devnexus2015.R;

import java.util.List;

/**
 * Created by summers on 1/7/16.
 */
public class BadgeContactViewAdapter extends RecyclerView.Adapter<BadgeContactViewAdapter.BadgeContactViewHolder> {

    private final List<BadgeContact> conacts;
    private final int columnCount;

    public BadgeContactViewAdapter(List<BadgeContact> badgeContacts, int columnCount) {
        this.conacts = badgeContacts;
        this.columnCount = columnCount;
    }


    @Override
    public BadgeContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View badgeLayout = inflater.inflate(R.layout.badge_layout, null);
        return new BadgeContactViewHolder(badgeLayout);

    }

    @Override
    public void onBindViewHolder(BadgeContactViewHolder holder, int position) {
        if (position < conacts.size()) {
            BadgeContact contact = conacts.get(position);
            holder.emailAddress.setText(contact.getEmail());
            holder.lastName.setText(contact.getLastName());
            holder.firstName.setText(contact.getFirstName());
            holder.title.setText(contact.getOrganization());
        } else {
            holder.emailAddress.setText("");
            holder.lastName.setText("");
            holder.firstName.setText("");
            holder.title.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return conacts.size();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        };
    }

    public void setContacts(List<BadgeContact> contacts) {
        this.conacts.clear();
        this.conacts.addAll(contacts);
    }

    public static class BadgeContactViewHolder extends RecyclerView.ViewHolder {

        private final View badgeLayout;
        private final TextView firstName, lastName, emailAddress, title;

        public BadgeContactViewHolder(View badgeLayout) {
            super(badgeLayout);
            this.badgeLayout = badgeLayout;
            firstName = (TextView) badgeLayout.findViewById(R.id.first_name);
            lastName = (TextView) badgeLayout.findViewById(R.id.last_name);
            emailAddress = (TextView) badgeLayout.findViewById(R.id.email);
            title = (TextView) badgeLayout.findViewById(R.id.title);
        }
    }

}
