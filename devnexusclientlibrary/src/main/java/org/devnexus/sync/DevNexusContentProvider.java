package org.devnexus.sync;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.CountDownCallback;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.PresentationContract;
import org.devnexus.vo.contract.PreviousYearPresentationContract;
import org.devnexus.vo.contract.ScheduleContract;
import org.devnexus.vo.contract.ScheduleItemContract;
import org.devnexus.vo.contract.SingleColumnJsonArrayList;
import org.devnexus.vo.contract.UserCalendarContract;
import org.jboss.aerogear.android.core.ReadFilter;
import org.jboss.aerogear.android.store.generator.DefaultIdGenerator;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by summers on 2/8/14.
 */
public class DevNexusContentProvider extends ContentProvider {


    private static final String TAG = DevNexusContentProvider.class.getSimpleName();
    private static final Gson GSON = GsonUtils.GSON;

    private static ContentResolver resolver;
    private static ArrayList<Presentation> presentations;
    private SQLStore<UserCalendar> calendarSQLStore;
    private SQLStore<Schedule> scheduleSQLStore;
    private SQLStore<Presentation> presentationSQLStore;
    private final CountDownLatch createdLatch = new CountDownLatch(3);

    private static ArrayList<Schedule> schedule = null;

    @Override
    public boolean onCreate() {
        resolver = getContext().getContentResolver();


        calendarSQLStore = new SQLStore<UserCalendar>(UserCalendar.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        calendarSQLStore.open(new CountDownCallback<SQLStore<UserCalendar>>(createdLatch));
        scheduleSQLStore = new SQLStore<Schedule>(Schedule.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        scheduleSQLStore.open(new CountDownCallback<SQLStore<Schedule>>(createdLatch));
        presentationSQLStore = new SQLStore<Presentation>(Presentation.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        presentationSQLStore.open(new CountDownCallback<SQLStore<Presentation>>(createdLatch));


        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uri.equals(UserCalendarContract.URI)) {
            return execute(uri, null, null, null, new CalendarQuery());
        } else if (uri.equals(ScheduleContract.URI)) {
            return execute(uri, null, null, null, new ScheduleQuery());
        } else if (uri.equals(ScheduleItemContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new ScheduleItemQuery());
        } else if (uri.equals(PresentationContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new PresentationQuery());
        } else if (uri.equals(PreviousYearPresentationContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new PreviousYearPresentationQuery(getContext()));
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
    }

    @Override
    public String getType(Uri uri) {
        if (uri.equals(UserCalendarContract.URI)) {
            return uri.toString();
        } else if (uri.equals(ScheduleContract.URI)) {
            return uri.toString();
        } else if (uri.equals(ScheduleItemContract.URI)) {
            return uri.toString();
        } else if (uri.equals(PresentationContract.URI)) {
            return uri.toString();
        } else if (uri.equals(PreviousYearPresentationContract.URI)) {
            return uri.toString();
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        if (uri.equals(UserCalendarContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new CalendarInsert());
        } else if (uri.equals(ScheduleContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new ScheduleInsert());
        } else if (uri.equals(PresentationContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new PresentationInsert());
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        if (uri.equals(UserCalendarContract.URI)) {
            Integer res = execute(uri, values, "", null, new CalendarBulkInsert());
            if (res == null) {
                return 0;
            } else {
                return res;
            }
        } else if (uri.equals(ScheduleContract.URI)) {
            Integer res = execute(uri, values, "", null, new ScheduleBulkInsert());
            if (res == null) {
                return 0;
            } else {
                return res;
            }
        } else if (uri.equals(PresentationContract.URI)) {
            Integer res = execute(uri, values, "", null, new PresentationBulkInsert());
            if (res == null) {
                return 0;
            } else {
                return res;
            }
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));

    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {

        Operation<Integer> op;

        if (uri.equals(UserCalendarContract.URI)) {
            op = new CalendarDelete();
        } else if (uri.equals(ScheduleContract.URI)) {
            op = new ScheduleDelete();
        } else if (uri.equals(PresentationContract.URI) ||uri.equals(PresentationContract.URI_NOTIFY)) {
            op = new PresentationDelete();
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));


        Integer res = execute(uri, null, selection, selectionArgs, op);
        if (res == null) {
            res = 0;
        }
        return res;

    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        Operation<Integer> op;
        ContentValues[] vals;
        if (uri.equals(UserCalendarContract.URI)) {
            if (values == null) {
                vals = new ContentValues[]{null};
                op = new CalendarUpdate();
            } else {
                vals = new ContentValues[]{values};
                op = new CalendarUpdate();
            }
        } else if (uri.equals(ScheduleContract.URI)) {
            if (values == null) {
                vals = new ContentValues[]{null};
                op = new ScheduleUpdate();

            } else {
                vals = new ContentValues[]{values};
                op = new ScheduleUpdate();

            }
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));

        Integer res = execute(uri, vals, selection, selectionArgs, op);
        if (res == null) {
            res = 0;
        }
        return res;

    }

    private <T> T execute(final Uri uri, final ContentValues[] values, final String selection, final String[] selectionArgs, final Operation<T> op) {
        final AtomicReference<T> returnRef = new AtomicReference<T>();

        try {
            createdLatch.await(20, TimeUnit.SECONDS);//make sure the databases were created.
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        SQLStore tempStore;
        if (uri.equals(UserCalendarContract.URI)) {
            tempStore = calendarSQLStore;
        } else if (uri.equals(ScheduleContract.URI) || uri.equals(ScheduleItemContract.URI) ) {
            tempStore = scheduleSQLStore;
        } else if (uri.equals(PresentationContract.URI) || uri.equals(PresentationContract.URI_NOTIFY) || uri.equals(PreviousYearPresentationContract.URI) ) {
            tempStore = presentationSQLStore;
        } else {
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
        }

        final SQLStore store = tempStore;


        synchronized (TAG) {
            returnRef.set(op.exec(GSON, store, uri, values, selection, selectionArgs));
        }
        return returnRef.get();
    }

    private interface Operation<T> {
        T exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs);
    }

    private static class CalendarInsert implements Operation<Uri> {

        @Override
        public Uri exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            UserCalendar calendar = gson.fromJson(values[0].getAsString(UserCalendarContract.DATA), UserCalendar.class);
            calendarStore.save(calendar);
            if (values[0].getAsBoolean(ScheduleContract.NOTIFY) != null && values[0].getAsBoolean(ScheduleContract.NOTIFY)) {
                resolver.notifyChange(UserCalendarContract.URI, null, false);
            }
            return UserCalendarContract.URI;
        }
    }

    private static class CalendarBulkInsert implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            for (ContentValues value : values) {
                UserCalendar calendar = gson.fromJson(value.getAsString(UserCalendarContract.DATA), UserCalendar.class);
                calendarStore.save(calendar);
            }
            resolver.notifyChange(UserCalendarContract.URI, null, false);
            return values.length;
        }
    }


    private static class CalendarDelete implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (selectionArgs == null || selectionArgs[0] == null) {
                calendarStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                calendarStore.remove(id);
            }
            resolver.notifyChange(UserCalendarContract.URI, null, false);
            return 1;
        }
    }

    private static class CalendarUpdate implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (selectionArgs == null || selectionArgs[0] == null) {
                calendarStore.reset();
            } else {
                Long id = Long.parseLong(selectionArgs[0]);
                calendarStore.remove(id);
            }

            UserCalendar calendar = gson.fromJson(values[0].getAsString(UserCalendarContract.DATA), UserCalendar.class);
            calendarStore.save(calendar);
            if (values[0].getAsBoolean(UserCalendarContract.NOTIFY) != null && values[0].getAsBoolean(UserCalendarContract.NOTIFY)) {
                resolver.notifyChange(UserCalendarContract.URI, null, false);
            }
            return 1;
        }
    }

    private static class CalendarQuery implements Operation<SingleColumnJsonArrayList> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            return new SingleColumnJsonArrayList(new ArrayList<UserCalendar>(calendarStore.readAll()));
        }
    }

    private static class ScheduleQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (DevNexusContentProvider.schedule == null) {
                DevNexusContentProvider.schedule = new ArrayList<Schedule>(scheduleStore.readAll());
            }
            return new SingleColumnJsonArrayList(new ArrayList<Schedule>(DevNexusContentProvider.schedule));
        }
    }


    private static class ScheduleItemQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (DevNexusContentProvider.schedule == null) {
                DevNexusContentProvider.schedule = new ArrayList<Schedule>(scheduleStore.readAll());
            }

            if (selection == null || selection.isEmpty()) {
                return new SingleColumnJsonArrayList(new ArrayList<ScheduleItem>(DevNexusContentProvider.schedule.get(0).scheduleItemList.scheduleItems));
            } else {

                ArrayList<ScheduleItem> items = new ArrayList<>(schedule.get(0).scheduleItemList.scheduleItems);
                ArrayList<ScheduleItem> filteredItems = new ArrayList<>(items.size());//max number

                String[] selections = selection.split(" && ");

                for (int i = 0; i <selections.length; i++) {
                    for (ScheduleItem item :items) {
                        String value = selectionArgs[i];
                        switch (selections[i])  {
                            case ScheduleItemContract.PRESENTATION_ID:
                                if ((item.presentation == null) || !Integer.toString(item.presentation.id).equals(value)) {
                                    filteredItems.add(item);
                                }
                                break;
                            case ScheduleItemContract.SPEAKER_FNAME:

                                break;
                            case ScheduleItemContract.SPEAKER_NAME:
                                break;
                            case ScheduleItemContract.TITLE:
                                break;
                            default:
                                break;
                        }

                    }

                }
                items.removeAll(filteredItems);
                return new SingleColumnJsonArrayList(items);
            }
        }
    }


    private static class ScheduleUpdate implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (selectionArgs == null || selectionArgs[0] == null) {
                scheduleStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                scheduleStore.remove(id);
            }
            Schedule schedule = gson.fromJson(values[0].getAsString(ScheduleContract.DATA), Schedule.class);
            scheduleStore.save(schedule);
            DevNexusContentProvider.schedule = null;
            DevNexusContentProvider.schedule = null;
            if (values[0].getAsBoolean(ScheduleContract.NOTIFY) != null && values[0].getAsBoolean(ScheduleContract.NOTIFY)) {
                resolver.notifyChange(ScheduleContract.URI, null, false);
            }
            return 1;
        }
    }

    private static class ScheduleBulkInsert implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            for (ContentValues value : values) {
                Schedule calendar = gson.fromJson(value.getAsString(ScheduleContract.DATA), Schedule.class);
                scheduleStore.save(calendar);
            }
            DevNexusContentProvider.schedule = null;
            resolver.notifyChange(ScheduleContract.URI, null, false);
            return values.length;
        }
    }


    private static class ScheduleDelete implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (selectionArgs == null || selectionArgs[0] == null) {
                scheduleStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                scheduleStore.remove(id);
            }
            DevNexusContentProvider.schedule = null;
            resolver.notifyChange(ScheduleContract.URI, null, false);
            return 1;
        }
    }

    public static class ScheduleInsert implements Operation<Uri> {

        @Override
        public Uri exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            Schedule calendar = gson.fromJson(values[0].getAsString(ScheduleContract.DATA), Schedule.class);
            scheduleStore.save(calendar);
            schedule = null;
            if (values[0].getAsBoolean(ScheduleContract.NOTIFY) != null && values[0].getAsBoolean(ScheduleContract.NOTIFY)) {
                resolver.notifyChange(ScheduleContract.URI, null, false);
            }
            return ScheduleContract.URI;
        }
    }


    public static class PresentationInsert implements Operation<Uri> {

        @Override
        public Uri exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            Presentation presentation = gson.fromJson(values[0].getAsString(PresentationContract.DATA), Presentation.class);
            presentationStore.save(presentation);
            schedule = null;
            if (values[0].getAsBoolean(PresentationContract.NOTIFY) != null && values[0].getAsBoolean(PresentationContract.NOTIFY)) {
                resolver.notifyChange(PresentationContract.URI, null, false);
            }
            return PresentationContract.URI;
        }
    }

    private static class PresentationBulkInsert implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            for (ContentValues value : values) {
                Presentation presentation = gson.fromJson(value.getAsString(PresentationContract.DATA), Presentation.class);
                presentationStore.save(presentation);
            }
            DevNexusContentProvider.schedule = null;
            resolver.notifyChange(PresentationContract.URI, null, false);
            return values.length;
        }
    }


    private static class PresentationQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (DevNexusContentProvider.presentations == null) {
                DevNexusContentProvider.presentations = new ArrayList<Presentation>(presentationStore.readAll());
            }

            if (selection == null ||selection.isEmpty()) {
                return new SingleColumnJsonArrayList(new ArrayList<Presentation>(DevNexusContentProvider.presentations));
            } else {
                String[] queries = selection.split(" && ");
                JSONObject where = new JSONObject();
                for (int i = 0; i <queries.length; i++) {
                    try {
                        where.put(queries[i], selectionArgs[i]);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                ReadFilter filter = new ReadFilter();

                filter.setWhere(where);

                return new SingleColumnJsonArrayList(new ArrayList<Presentation>(presentationStore.readWithFilter(filter)));
            }
        }
    }


    private static class PreviousYearPresentationQuery implements Operation<Cursor> {

        private final Context context;

        private PreviousYearPresentationQuery(Context context) {
            this.context = context;
        }

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            

            if (selection == null ||selection.isEmpty()) {
                return new SingleColumnJsonArrayList(new ArrayList<Presentation>(0));//No Event selected
            } else {
                
                String[] queries = selection.split(" && ");
                JSONObject where = new JSONObject();
                for (int i = 0; i <queries.length; i++) {
                    try {
                        where.put(queries[i], selectionArgs[i]);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                try {
                    String eventId = where.getString(PreviousYearPresentationContract.EVENT_LABEL);
                    PreviousYearPresentationContract.Events event = PreviousYearPresentationContract.Events.fromLabel(eventId);

                    PresentationResponse presentationsObject = gson.fromJson(IOUtils.toString(context.getResources().openRawResource(event.getRawResourceId())), PresentationResponse.class);
                    List<Presentation> presentations = presentationsObject.presentationList.presentation;
                    Iterator<String> keys = where.keys();
                    
                    while(keys.hasNext()) {
                        String key = keys.next();
                        if (key == PreviousYearPresentationContract.EVENT_LABEL) {
                            continue;
                        }
                        
                        if (key.equals(PreviousYearPresentationContract.PRESENTATION_ID)) {
                            for (Presentation presentation :presentations) {
                                if (Integer.toString(presentation.getId()).equals(where.getString(key))){
                                    ArrayList<Presentation> toReturn = new ArrayList<>(1);
                                    toReturn.add(presentation);
                                    return new SingleColumnJsonArrayList(toReturn);
                                }
                            }
                        }
                        
                    }
                    
                    return new SingleColumnJsonArrayList(presentations);
                    
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return new SingleColumnJsonArrayList(new ArrayList<Presentation>(0));//No Event selected
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return new SingleColumnJsonArrayList(new ArrayList<Presentation>(0));//No Event selected
                }


            }
        }
    }


    private static class PresentationDelete implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (selectionArgs == null || selectionArgs[0] == null) {
                presentationStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                presentationStore.remove(id);
            }
            DevNexusContentProvider.presentations = null;

            if (uri.equals(PresentationContract.URI_NOTIFY)) {
                resolver.notifyChange(PresentationContract.URI, null, false);
            }

            return 1;
        }
    }


}
