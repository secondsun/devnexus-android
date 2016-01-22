package org.jboss.aerogear.devnexus2015.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.devnexus.util.AccountUtil;
import org.devnexus.util.GsonUtils;
import org.devnexus.util.StringUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.UserScheduleItem;
import org.devnexus.vo.contract.ScheduleItemContract;
import org.devnexus.vo.contract.UserCalendarContract;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadUserCalendarFromDevNexusTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final ContentResolver resolver;

    public LoadUserCalendarFromDevNexusTask(Context context, ContentResolver resolver) {
        this.context = context.getApplicationContext();
        this.resolver = resolver;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String googleToken = AccountUtil.getCookie(context);

            if (StringUtils.isEmpty(googleToken)) {//Not logged in, do nothing.
                return null;
            }

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://devnexus.com/s/user-schedule")
                    .get()
                    .header("authToken", googleToken)
                    .build();

            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            client.setWriteTimeout(30, TimeUnit.SECONDS);
            Response response = client.newCall(request).execute();

            String responseString = response.body().string();

            UserScheduleItem[] userScheduleItems = GsonUtils.GSON.fromJson(responseString, UserScheduleItem[].class);

            SparseArray<ScheduleItem> scheduleItems = new SparseArray<>(200);

            Cursor scheduleItemCursor = resolver.query(ScheduleItemContract.URI, null, null, null, null);

            while (scheduleItemCursor.moveToNext()) {
                ScheduleItem item = GsonUtils.GSON.fromJson(scheduleItemCursor.getString(0), ScheduleItem.class);
                scheduleItems.put(item.id, item);
            }
            scheduleItemCursor.close();


            for (UserScheduleItem item : userScheduleItems) {
                ScheduleItem scheduleItem = scheduleItems.get(item.scheduleItemId.intValue());

                Cursor userItemCursor = resolver.query(UserCalendarContract.URI, null, UserCalendarContract.START_TIME, new String[]{scheduleItem.fromTime.getTime() + ""}, null);
                userItemCursor.moveToFirst();
                UserCalendar userItem = GsonUtils.GSON.fromJson(userItemCursor.getString(0), UserCalendar.class);
                userItemCursor.close();

                Presentation presentation = scheduleItem.presentation;

                scheduleItem = new ScheduleItem();
                scheduleItem.presentation = presentation;

                userItem.items.add( scheduleItem );

                resolver.update(UserCalendarContract.URI, UserCalendarContract.valueize(userItem, false), UserCalendarContract.ID, new String[]{userItem.getId() + ""});
            }

            resolver.notifyChange(UserCalendarContract.URI, null);


        } catch (Exception ignore) {
            Log.d("LOAD_SCHEDULE", ignore.getMessage(), ignore);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Fetch complete.", Toast.LENGTH_LONG).show();
    }
}
