package org.devnexus.util;

import android.util.Log;


import java.util.concurrent.CountDownLatch;
import org.jboss.aerogear.android.Callback;

/**
 * Created by summers on 12/12/13.
 */
public class CountDownCallback<T> implements Callback<T> {

    private static final String TAG = CountDownCallback.class.getSimpleName();
    private final CountDownLatch latch;

    public CountDownCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onSuccess(T data) {
        latch.countDown();
    }

    @Override
    public void onFailure(Exception e) {
        latch.countDown();
        Log.e(TAG, e.getMessage(), e);
    }
}
