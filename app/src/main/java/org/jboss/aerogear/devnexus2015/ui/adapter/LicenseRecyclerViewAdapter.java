package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.License;

import java.util.ArrayList;

/**
 * Created by summers on 1/10/16.
 */
public class LicenseRecyclerViewAdapter extends RecyclerView.Adapter<LicenseRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<License> licenses;
    private final Context mContext;

    public LicenseRecyclerViewAdapter(ArrayList<License> licenses, Context context) {
        this.licenses = licenses;
        this.mContext = context.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.license_template, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final License license = licenses.get(position);
        holder.licenseText.setText(license.text);
        holder.projectName.setText(license.project);
        holder.projectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = license.link;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return licenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView licenseText;
        private final TextView projectName;

        public ViewHolder(View itemView) {
            super(itemView);
            projectName = (TextView)itemView.findViewById(R.id.project_name);
            licenseText = (TextView)itemView.findViewById(R.id.license_text);
        }
    }
}
