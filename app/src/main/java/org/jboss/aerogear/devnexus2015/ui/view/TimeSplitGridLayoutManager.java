package org.jboss.aerogear.devnexus2015.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by summers on 3/7/15.
 */
public class TimeSplitGridLayoutManager extends GridLayoutManager {
    public TimeSplitGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public TimeSplitGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }
    
    
}
