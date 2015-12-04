package org.jboss.aerogear.devnexus2015.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;

import com.google.maps.android.ui.IconGenerator;

import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.RoomMetaData;

public class GWCCMapIconGenerator {

    private final Context context;
    

    public GWCCMapIconGenerator(Context c) {
        this.context = c;
        
    }

    public Bitmap getIcon(RoomMetaData room, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setTextAppearance(R.style.MapLabel);
        iconGenerator.setContentPadding(3,3,3,3);
        iconGenerator.setBackground(new ColorDrawable(room.color));
        return iconGenerator.makeIcon(title);

    }


}
