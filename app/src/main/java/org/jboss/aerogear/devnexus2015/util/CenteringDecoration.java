package org.jboss.aerogear.devnexus2015.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by summers on 12/8/15.
 */
public class CenteringDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int viewWidth;


    public CenteringDecoration(int spanCount, int viewWidthDp, Context context) {
        this.spanCount = spanCount;

        Resources r = context.getResources();
        this.viewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, viewWidthDp, r.getDisplayMetrics());


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.set(0,0,0,0);

        GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup();
        int rowSpanCount = this.spanCount;
        int adapterPosition = parent.getChildAdapterPosition(view);
        int spanSize = lookup.getSpanSize(adapterPosition);

        if (((LinearLayout)view).getChildCount() != 1) {//short circuit if header
            return;
        }

        int spanPosition = 0;

        for (int itemindex = 0; itemindex < adapterPosition; itemindex++) {
            spanSize = lookup.getSpanSize(itemindex);
            if (spanSize == this.spanCount) {//short circuit if header
                spanPosition = 0;
            }  else {
                spanPosition++;
            }
        }

        spanPosition = spanPosition % spanCount;

        //calculate how many items are on this row
        if (spanPosition != rowSpanCount - 1) {//we are not at the end of a row, peek
            rowSpanCount = spanPosition + 1;//counts vs positions
            for ( int itemIndex = (adapterPosition + 1); itemIndex < parent.getAdapter().getItemCount(); itemIndex++) {
                spanSize = lookup.getSpanSize(itemIndex);
                if (spanSize == this.spanCount) {//short circuit if header
                    break;
                }  else {
                    rowSpanCount++;
                    if (rowSpanCount== this.spanCount) {//short circuit if full
                        break;
                    }
                }
            }
        }

        final int[] originalLefts, originalRights;
        originalLefts = new int[spanCount];
        originalRights = new int[spanCount];

        int runningLeft = 0;
        for (int counter = 0; counter < spanCount; counter++) {
            originalLefts[counter] = runningLeft;
            originalRights[counter] = runningLeft + (parent.getWidth() / spanCount);
            runningLeft += parent.getWidth() / spanCount;
        }

        int newFarLeft = (parent.getWidth()  - viewWidth * rowSpanCount) / 2;

        if (rowSpanCount != 1) {
            outRect.left = newFarLeft + viewWidth * spanPosition - originalLefts[spanPosition];
            outRect.right = (newFarLeft +(parent.getWidth() / spanCount)) + viewWidth * (spanPosition+1) - originalRights[spanPosition];
        } else {
            outRect.left = newFarLeft + viewWidth * spanPosition - originalLefts[spanPosition];
            outRect.right = parent.getWidth();
        }


    }
}
