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
        int rowItemsCount = this.spanCount;
        int adapterPosition = parent.getChildAdapterPosition(view);
        int spanSize = lookup.getSpanSize(adapterPosition);

        if (((LinearLayout)view).getChildCount() != 1) {//short circuit if header
            return;
        }

        int columnPosition = 0;

        for (int itemindex = 0; itemindex < adapterPosition; itemindex++) {
            spanSize = lookup.getSpanSize(itemindex);
            if (spanSize == this.spanCount) {//short circuit if header
                columnPosition = 0;
            }  else {
                columnPosition++;
            }
        }

        columnPosition = columnPosition % spanCount;

        //calculate how many items are on this row
        if (columnPosition != rowItemsCount - 1) {//we are not at the end of a row, peek
            rowItemsCount = columnPosition + 1;//counts vs positions
            for ( int itemIndex = (adapterPosition + 1); itemIndex < parent.getAdapter().getItemCount(); itemIndex++) {
                spanSize = lookup.getSpanSize(itemIndex);
                if (spanSize == this.spanCount) {//short circuit if header
                    break;
                }  else {
                    rowItemsCount++;
                    if (rowItemsCount== this.spanCount) {//short circuit if full
                        break;
                    }
                }
            }
        }


        int originalLeft = (parent.getWidth() / spanCount) * columnPosition;
        int originalRight = (parent.getWidth() / spanCount) * (columnPosition + 2);
        if (rowItemsCount == 1) {
            originalLeft = 0;
            originalRight = parent.getWidth();
        }

        int newFarLeft = (parent.getWidth()  - viewWidth * rowItemsCount) / 2;

        int newLeft = newFarLeft + (this.viewWidth * columnPosition);
        int newRight =newLeft + viewWidth;

        outRect.left = newLeft - originalLeft;
        outRect.right = newRight - originalRight;

    }
}
