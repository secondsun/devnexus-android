package org.jboss.aerogear.devnexus2015.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by summers on 1/4/15.
 */
public class PatchedSpinner extends Spinner {

    public PatchedSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PatchedSpinner(Context context) {
        super(context);
    }

    public PatchedSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        // this line removed, we do not want to delegate the click to the spinner.
        // boolean handled = super.performClick();

        Context context = getContext();

        final DropDownAdapter adapter = new DropDownAdapter(getAdapter());

        CharSequence mPrompt = getPrompt();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (mPrompt != null) {
            builder.setTitle(mPrompt);
        }
        builder.setSingleChoiceItems(adapter, getSelectedItemPosition(), this).show();

        return true;
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;

        public DropDownAdapter(SpinnerAdapter adapter) {
            mAdapter = adapter;
        }

        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        public Object getItem(int position) {
            return mAdapter == null ? null : mAdapter.getItem(position);
        }

        public long getItemId(int position) {
            return mAdapter == null ? -1 : mAdapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            if (mAdapter == null) {
                return null;
            }
           return mAdapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer){
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        // PATCHED
        public boolean areAllItemsEnabled() {
            if (mAdapter instanceof BaseAdapter) {
                return ((BaseAdapter) mAdapter).areAllItemsEnabled();
            } else {
                return true;
            }
        }

        // PATCHED
        public boolean isEnabled(int position) {
            if (mAdapter instanceof BaseAdapter) {
                return ((BaseAdapter) mAdapter).isEnabled(position);
            } else {
                return true;
            }
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }
}
