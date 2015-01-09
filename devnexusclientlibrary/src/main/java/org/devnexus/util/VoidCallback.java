package org.devnexus.util;

import org.jboss.aerogear.android.core.Callback;

/**
 * Created by summers on 2/16/14.
 */
public class VoidCallback<T> implements Callback<T> {
    public static VoidCallback INSTANCE = new VoidCallback();

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onFailure(Exception e) {

    }
}
