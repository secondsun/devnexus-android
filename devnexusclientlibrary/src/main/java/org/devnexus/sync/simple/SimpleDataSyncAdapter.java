package org.devnexus.sync.simple;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.contract.PresentationContract;
import org.devnexus.vo.contract.ScheduleContract;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * This class downloads relatively static data that needs no authentication nor upstream.
 */
public class SimpleDataSyncAdapter  extends AbstractThreadedSyncAdapter {


    private static final Pipe<Schedule> SCHEDULE_PIPE;
    private static final Pipe<PresentationResponse> PRESENTATION_PIPE;

    static {
        try {
            SCHEDULE_PIPE =PipeManager.config("schedule", RestfulPipeConfiguration.class)
                .withUrl(new URL("https://devnexus.com/api/schedule.json"))
                .responseParser(new GsonResponseParser(GsonUtils.GSON))
                .forClass(Schedule.class);
            PRESENTATION_PIPE =PipeManager.config("presentations", RestfulPipeConfiguration.class)
                    .withUrl(new URL("https://devnexus.com/api/presentations.json"))
                    .responseParser(new GsonResponseParser(GsonUtils.GSON))
                    .forClass(PresentationResponse.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    ContentResolver mContentResolver;
    /**
     * Set up the sync adapter
     */
    public SimpleDataSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public SimpleDataSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final CountDownLatch syncFinished = new CountDownLatch(2);

        SCHEDULE_PIPE.read(new Callback<List<Schedule>>() {
            @Override
            public void onSuccess(List<Schedule> schedules) {
                mContentResolver.delete(ScheduleContract.URI, "", null);
                ContentValues scheduleValues = ScheduleContract.valueize(schedules.get(0));
                scheduleValues.put(ScheduleContract.NOTIFY, true);
                mContentResolver.insert(ScheduleContract.URI, scheduleValues);
                Log.d("DevNexus", "Schedules Saved");
                syncFinished.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DevNexus", e.getMessage(), e);
                syncFinished.countDown();
            }
        });

        PRESENTATION_PIPE.read(new Callback<List<PresentationResponse>>() {
            @Override
            public void onSuccess(List<PresentationResponse> presentationResponses) {
                mContentResolver.delete(PresentationContract.URI, "", null);
                ContentValues[] scheduleValues = PresentationContract.valueize(presentationResponses.get(0).presentations);

                mContentResolver.bulkInsert(PresentationContract.URI, scheduleValues);
                Log.d("DevNexus", "Schedules Saved");
                syncFinished.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DevNexus", e.getMessage(), e);
                syncFinished.countDown();
            }
        });

        try {
            syncFinished.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
