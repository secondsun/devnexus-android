package org.jboss.aerogear.devnexus2015.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.PresentationContract;
import org.devnexus.vo.contract.ScheduleContract;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;
import org.jboss.aerogear.devnexus2015.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class downloads relatively static data that needs no authentication nor upstream.
 */
public class LiveDataSyncAdapter extends AbstractThreadedSyncAdapter {


    private static final Pipe<Schedule> SCHEDULE_PIPE;
    private static final Pipe<PresentationResponse> PRESENTATION_PIPE;
    public static final String SYNC_CALENDAR = "devnexus.org.account.SYNC_USER_CALENDAR";

    static {
        try {
            SCHEDULE_PIPE = PipeManager.config("schedule", RestfulPipeConfiguration.class)
                    .withUrl(new URL("https://devnexus.com/api/schedule.json"))
                    .responseParser(new GsonResponseParser(GsonUtils.GSON))
                    .forClass(Schedule.class);
            PRESENTATION_PIPE = PipeManager.config("presentations", RestfulPipeConfiguration.class)
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
    public LiveDataSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    public LiveDataSyncAdapter(
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
    public synchronized void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final CountDownLatch syncFinished = new CountDownLatch(2);

        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(ScheduleContract.URI, null, null, null, null);
            if (cursor.getCount() == 0) {
                loadStatic();
                return;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        SCHEDULE_PIPE.read(new Callback<List<Schedule>>() {
            @Override
            public void onSuccess(List<Schedule> schedules) {
                ContentValues scheduleValues = ScheduleContract.valueize(schedules.get(0));
                scheduleValues.put(ScheduleContract.NOTIFY, true);
                mContentResolver.update(ScheduleContract.URI, scheduleValues, null, null);
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
                //mContentResolver.delete(PresentationContract.URI, "", null);
                List<Presentation> presentations = presentationResponses.get(0).presentations;

                for (Presentation presentation : presentations) {
                    ContentValues scheduleValues = PresentationContract.valueize(presentation);
                    Cursor presentationCursor = mContentResolver.query(PresentationContract.URI, null, PresentationContract._ID, new String[]{presentation.id + ""}, null);
                    if (presentationCursor.moveToFirst()) {
                        mContentResolver.update(PresentationContract.URI, scheduleValues, PresentationContract._ID, new String[]{presentation.id + ""});
                    } else {
                        mContentResolver.insert(PresentationContract.URI, scheduleValues);
                    }
                    presentationCursor.close();
                }


                Log.d("DevNexus", "Presentations Saved");
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

        if (AccountManager.get(getContext()).getUserData(account, SYNC_CALENDAR) != null) {

        }

    }

    private void loadStatic() {

        InputStream scheduleStream = getContext().getResources().openRawResource(R.raw.schedule);
        String scheduleTemplateJson = null;
        try {
            scheduleTemplateJson = IOUtils.toString(scheduleStream);
        } catch (IOException ignore) {
        }


        InputStream presentationStream = getContext().getResources().openRawResource(R.raw.presentations);
        String presentationTemplateJson = null;
        try {
            presentationTemplateJson = IOUtils.toString(presentationStream);
        } catch (IOException ignore) {
        }


        Schedule schedules = GsonUtils.GSON.fromJson(scheduleTemplateJson, Schedule.class);

        ContentValues scheduleValues = ScheduleContract.valueize(schedules);
        scheduleValues.put(ScheduleContract.NOTIFY, true);


        PresentationResponse presentationResponses = GsonUtils.GSON.fromJson(presentationTemplateJson, PresentationResponse.class);

        mContentResolver.insert(ScheduleContract.URI, scheduleValues);
        ContentValues[] presentationValues = PresentationContract.valueize(presentationResponses.presentations);

        mContentResolver.bulkInsert(PresentationContract.URI, presentationValues);

    }
}
