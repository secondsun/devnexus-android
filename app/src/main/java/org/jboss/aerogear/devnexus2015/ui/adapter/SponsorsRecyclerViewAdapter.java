package org.jboss.aerogear.devnexus2015.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.Sponsor;

import java.util.ArrayList;

/**
 * Created by summers on 1/10/16.
 */
public class SponsorsRecyclerViewAdapter extends RecyclerView.Adapter<SponsorsRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<ItemOrHeader> items;
    private final Context mContext;
    private final int columnCount;

    public SponsorsRecyclerViewAdapter(ArrayList<Sponsor> sponsors, Context context, int columnCount) {
        this.items = makeList(sponsors);
        mContext = context;
        this.columnCount = columnCount;
    }

    private ArrayList<ItemOrHeader> makeList(ArrayList<Sponsor> sponsors) {
        ArrayList<ItemOrHeader> toReturn = new ArrayList<>(sponsors.size() * 3);
        Sponsor previousItem = null;
        for (Sponsor item : sponsors) {
            if (previousItem == null || !item.sponsorLevel.equals(previousItem.sponsorLevel)) {
                ItemOrHeader header = new ItemOrHeader(item.getLevelEnum().title, null);
                toReturn.add(header);
            }
            toReturn.add(new ItemOrHeader(null, item));

            previousItem = item;
        }
        return toReturn;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType() == ItemOrHeader.TYPE.HEADER ? 0 : 1;
    }

    @Override
    public SponsorsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = null;
        switch (viewType) {
            case 0:
                v = inflater.inflate(R.layout.sponsor_level_header_layout, null);
                break;
            case 1:
                v = inflater.inflate(R.layout.sponsor_item_layout, null);
                break;
        }


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SponsorsRecyclerViewAdapter.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case 0:
                holder.header.setText(items.get(position).header);
                break;
            case 1:
                Picasso.with(mContext).load("https://devnexus.com/api/sponsors/" + items.get(position).item.id + ".jpg").placeholder(new ColorDrawable(0xfff)).fit().centerCrop().noFade().into(holder.image);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = items.get(position).item.link;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        mContext.startActivity(i);
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getItemViewType(position)) {
                    case 0:
                        return columnCount;
                    default:
                        return 1;
                }
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView header;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.sponsor_image);
            header = (TextView) itemView.findViewById(R.id.sponsor_level);
        }
    }


    private static class ItemOrHeader {

        enum TYPE {HEADER, ITEM};

        final String header;
        final Sponsor item;

        private ItemOrHeader(String header, Sponsor item) {
            this.header = header;
            this.item = item;
        }

        TYPE getType() {
            return item == null ? TYPE.HEADER : TYPE.ITEM;
        }

        <T> T get() {
            return (T) (item == null ? header : item);
        }

    }


}
