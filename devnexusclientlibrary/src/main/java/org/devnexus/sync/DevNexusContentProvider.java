package org.devnexus.sync;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.devnexus.R;
import org.devnexus.util.CountDownCallback;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.BadgeContact;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.Room;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.ScheduleItemType;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.BadgeContactContract;
import org.devnexus.vo.contract.PresentationContract;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static Context context;
    private static ArrayList<Presentation> presentations;
    private static ArrayList<Schedule> schedule = null;
    private final CountDownLatch createdLatch = new CountDownLatch(4);
    private SQLStore<UserCalendar> userCalendarStore;
    private SQLStore<Schedule> scheduleSQLStore;
    private SQLStore<Presentation> presentationSQLStore;
    private SQLStore<BadgeContact> badgeContactSQLStore;

    @Override
    public boolean onCreate() {
        resolver = getContext().getContentResolver();
        context = getContext();

        userCalendarStore = new SQLStore<UserCalendar>(UserCalendar.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        userCalendarStore.open(new CountDownCallback<SQLStore<UserCalendar>>(createdLatch));
        scheduleSQLStore = new SQLStore<Schedule>(Schedule.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        scheduleSQLStore.open(new CountDownCallback<SQLStore<Schedule>>(createdLatch));
        presentationSQLStore = new SQLStore<Presentation>(Presentation.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        presentationSQLStore.open(new CountDownCallback<SQLStore<Presentation>>(createdLatch));
        badgeContactSQLStore = new SQLStore<BadgeContact>(BadgeContact.class, getContext(), GsonUtils.builder(), new DefaultIdGenerator());
        badgeContactSQLStore.open(new CountDownCallback<SQLStore<BadgeContact>>(createdLatch));

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uri.equals(UserCalendarContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new UserCalendarQuery());
        } else if (uri.equals(ScheduleContract.URI)) {
            return execute(uri, null, null, null, new ScheduleQuery());
        } else if (uri.equals(ScheduleItemContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new ScheduleItemQuery());
        } else if (uri.equals(PresentationContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new PresentationQuery());
        } else if (uri.equals(BadgeContactContract.URI)) {
            return execute(uri, null, selection, selectionArgs, new BadgeContactQuery());
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
        } else if (uri.equals(BadgeContactContract.URI)) {
            return uri.toString();
        } else
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        if (uri.equals(UserCalendarContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new UserCalendarInsert());
        } else if (uri.equals(ScheduleContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new ScheduleInsert());
        } else if (uri.equals(PresentationContract.URI)) {
            return execute(uri, new ContentValues[]{values}, null, null, new PresentationInsert());
        } else if (uri.equals(BadgeContactContract.URI) || uri.equals(BadgeContactContract.URI_NOTIFY)) {
            return execute(uri, new ContentValues[]{values}, null, null, new BadgeContactInsert());
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
        } else if (uri.equals(BadgeContactContract.URI) || uri.equals(BadgeContactContract.URI_NOTIFY)) {
            Integer res = execute(uri, values, "", null, new BadgeContactBulkInsert());
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
            op = new UserCalendarDelete();
        } else if (uri.equals(ScheduleContract.URI)) {
            op = new ScheduleDelete();
        } else if (uri.equals(PresentationContract.URI) || uri.equals(PresentationContract.URI_NOTIFY)) {
            op = new PresentationDelete();
        } else if (uri.equals(BadgeContactContract.URI) || uri.equals(BadgeContactContract.URI_NOTIFY)) {
            op = new BadgeContactDelete();
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
                op = new UserCalendarUpdate();
            } else {
                vals = new ContentValues[]{values};
                op = new UserCalendarUpdate();
            }
        } else if (uri.equals(PresentationContract.URI)) {
            if (values == null) {
                vals = new ContentValues[]{null};
                op = new PresentationUpdate();
            } else {
                vals = new ContentValues[]{values};
                op = new PresentationUpdate();
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
            tempStore = userCalendarStore;
        } else if (uri.equals(ScheduleContract.URI) || uri.equals(ScheduleItemContract.URI)) {
            tempStore = scheduleSQLStore;
        } else if (uri.equals(PresentationContract.URI) || uri.equals(PresentationContract.URI_NOTIFY)) {
            tempStore = presentationSQLStore;
        } else if (uri.equals(BadgeContactContract.URI) || uri.equals(BadgeContactContract.URI_NOTIFY)) {
            tempStore = badgeContactSQLStore;
        } else {
            throw new IllegalArgumentException(String.format("%s not supported", uri.toString()));
        }

        final SQLStore store = tempStore;


        synchronized (store) {
            returnRef.set(op.exec(GSON, store, uri, values, selection, selectionArgs));
        }
        return returnRef.get();
    }

    private interface Operation<T> {
        T exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs);
    }

    private static class UserCalendarInsert implements Operation<Uri> {

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


    private static class UserCalendarDelete implements Operation<Integer> {

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

    private static class UserCalendarUpdate implements Operation<Integer> {

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

    private static class UserCalendarQuery implements Operation<SingleColumnJsonArrayList> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore calendarStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            List<UserCalendar> results = new ArrayList<UserCalendar>(calendarStore.readAll());
            if (results.isEmpty() || results.get(0).fromTime.getYear() < 116) {
                results = loadTemplate(calendarStore);
            }


            Collections.sort(results);

            if (selection != null && selection.contains(UserCalendarContract.DATE)) {
                List<UserCalendar> toRemove = new ArrayList<>(results.size());
                int dateIndex = Integer.parseInt(selectionArgs[0]);
                Date startDate = UserCalendarContract.DATES.get(dateIndex);
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.DATE, 1);
                Date startDateNextDay = cal.getTime();
                for (UserCalendar userCalendarItem : results) {
                    if (!(userCalendarItem.fromTime.after(startDate) && userCalendarItem.fromTime.before(startDateNextDay))) {
                        toRemove.add(userCalendarItem);
                    }
                }
                results.removeAll(toRemove);
                Collections.sort(results);
            } else if (selection != null && selection.contains(UserCalendarContract.PRESENTATION_ID)) {
                List<UserCalendar> toRemove = new ArrayList<>(results.size());
                int presentationId = Integer.parseInt(selectionArgs[0]);

                for (UserCalendar userCalendarItem : results) {
                    boolean remove = true;
                    for (ScheduleItem item : userCalendarItem.items) {
                        if (item.presentation.id == presentationId) {
                            remove = false;
                            break;
                        }
                    }
                    if (remove) {
                        toRemove.add(userCalendarItem);
                    }

                }
                results.removeAll(toRemove);
                Collections.sort(results);
            } else if (selection != null && selection.contains(UserCalendarContract.START_TIME)) {
                List<UserCalendar> toRemove = new ArrayList<>(results.size());
                Date startTime = new Date(Long.parseLong(selectionArgs[0]));

                for (UserCalendar userCalendarItem : results) {
                    if (userCalendarItem.fromTime.compareTo(startTime) != 0) {
                        toRemove.add(userCalendarItem);
                    }
                }
                results.removeAll(toRemove);
                Collections.sort(results);
            }

            //TODO: Replace all instance where I attach a new schedule item to a calendar with an appropriate schedule item and remove this workaround.
            ArrayList<ScheduleItem> items = new ArrayList<>(schedule.get(0).scheduleItems);
            SparseArray<ScheduleItem> mappedItems = new SparseArray<>(items.size());
            for (ScheduleItem item : items) {
                if (item.presentation != null) {
                    mappedItems.put(item.presentation.id, item);
                }
            }

            for (UserCalendar calendarItem : results) {
                if (calendarItem.items != null && calendarItem.items.size() > 0) {
                    for (ScheduleItem item : calendarItem.items) {
                        if (item.presentation != null && item.room == null) {
                            //TODO: Replace all instance where I attach a new schedule item to a calendar with an appropriate schedule item and remove this workaround.
                            ScheduleItem fullItem = mappedItems.get(item.presentation.id);
                            if (fullItem != null) {
                                item.room = new Room();
                            }
                        }
                    }
                }
            }

            return new SingleColumnJsonArrayList(new ArrayList<UserCalendar>(results));

        }

        private List<UserCalendar> loadTemplate(SQLStore<UserCalendar> calendarSQLStore) {

            InputStream templateStream = context.getResources().openRawResource(R.raw.schedule);
            String scheduleDataJson = null;
            try {
                scheduleDataJson = IOUtils.toString(templateStream);
            } catch (IOException ignore) {
                Log.e(TAG, ignore.getMessage(), ignore);
            }
            Schedule schedule = GSON.fromJson(scheduleDataJson, Schedule.class);
            Set<UserCalendar> userCalendarItems = new HashSet<>(schedule.scheduleItems.size());
            for (ScheduleItem item : schedule.scheduleItems) {
                UserCalendar uc = new UserCalendar();
                uc.id = item.id;
                uc.fromTime = item.fromTime;
                uc.duration = (int) ((item.toTime.getTime() - item.fromTime.getTime()) / 60000);
                if (ScheduleItemType.SESSION.name().equalsIgnoreCase(item.scheduleItemType)) {
                    uc.fixed = false;
                    uc.fixedTitle = "";
                } else {
                    uc.fixed = true;
                    uc.fixedTitle = item.title;
                    uc.room = item.room.name;
                    uc.color = item.room.color;
                    if (item.presentation != null) {
                        uc.fixedTitle = item.presentation.title;
                        uc.items.add(item);
                    }
                }
                userCalendarItems.add(uc);
            }


            ArrayList<UserCalendar> toReturn = new ArrayList<>(userCalendarItems);
            Collections.sort(toReturn);
            if (userCalendarItems.size() == 1 || toReturn.size() == 1) {
                return toReturn;
            }
            calendarSQLStore.reset();
            calendarSQLStore.save(toReturn);
            return toReturn;
        }
    }

    private static class ScheduleQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (DevNexusContentProvider.schedule == null) {
                DevNexusContentProvider.schedule = new ArrayList<Schedule>(scheduleStore.readAll());
                if (schedule.isEmpty()) {
                    loadTemplate(scheduleStore);
                }
            }
            return new SingleColumnJsonArrayList(new ArrayList<Schedule>(DevNexusContentProvider.schedule));
        }

        private void loadTemplate(SQLStore scheduleStore) {
            InputStream templateStream = context.getResources().openRawResource(R.raw.schedule);
            String scheduleDataJson = null;
            try {
                scheduleDataJson = IOUtils.toString(templateStream);
            } catch (IOException ignore) {
                Log.e(TAG, ignore.getMessage(), ignore);
            }
            schedule.add(GSON.fromJson(scheduleDataJson, Schedule.class));
            scheduleStore.reset();
            scheduleStore.save(schedule);
        }
    }

    private static class BadgeContactQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore badgeContactStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            return new SingleColumnJsonArrayList(new ArrayList<BadgeContact>(badgeContactStore.readAll()));
        }
    }


    private static class ScheduleItemQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (DevNexusContentProvider.schedule == null || DevNexusContentProvider.schedule.isEmpty()) {
                DevNexusContentProvider.schedule = new ArrayList<Schedule>(scheduleStore.readAll());
                if (schedule.isEmpty()) {
                    loadTemplate(scheduleStore);
                }
            }

            if (selection == null || selection.isEmpty()) {
                return new SingleColumnJsonArrayList(new ArrayList<ScheduleItem>(DevNexusContentProvider.schedule.get(0).scheduleItems));
            } else {

                ArrayList<ScheduleItem> items = new ArrayList<>(schedule.get(0).scheduleItems);
                ArrayList<ScheduleItem> filteredItems = new ArrayList<>(items.size());//max number

                String[] selections = selection.split(" && ");

                for (int i = 0; i < selections.length; i++) {
                    for (ScheduleItem item : items) {
                        String value = selectionArgs[i];
                        switch (selections[i]) {
                            case ScheduleItemContract.PRESENTATION_ID:
                                if ((item.presentation == null) || !Integer.toString(item.presentation.id).equals(value)) {
                                    filteredItems.add(item);
                                }
                                break;
                            case ScheduleItemContract.FROM_TIME:
                                if ((item.fromTime == null) || !(item.fromTime.equals(new Date(Long.parseLong(value))))) {
                                    filteredItems.add(item);
                                }
                                break;
                            case ScheduleItemContract.TRACK:
                                if ((item.presentation == null) || !item.presentation.track.name.equals(value)) {
                                    if (!value.equals("All Topics")) {
                                        filteredItems.add(item);
                                    }
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

        private void loadTemplate(SQLStore scheduleStore) {
            InputStream templateStream = context.getResources().openRawResource(R.raw.schedule);
            String scheduleDataJson = null;
            try {
                scheduleDataJson = IOUtils.toString(templateStream);
            } catch (IOException ignore) {
                Log.e(TAG, ignore.getMessage(), ignore);
            }
            schedule.add(GSON.fromJson(scheduleDataJson, Schedule.class));
            scheduleStore.reset();
            scheduleStore.save(schedule);
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
            DevNexusContentProvider.schedule = (ArrayList<Schedule>) scheduleStore.readAll();
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
                DevNexusContentProvider.schedule = (ArrayList<Schedule>) scheduleStore.readAll();
                resolver.notifyChange(ScheduleContract.URI, null, false);
                return values.length;

        }
    }


    private static class BadgeContactBulkInsert implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore badgeContactStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            List<BadgeContact> badgeContactsList = new ArrayList<>(values.length);
            for (ContentValues value : values) {
                BadgeContact badgeContact = gson.fromJson(value.getAsString(BadgeContactContract.DATA), BadgeContact.class);
                badgeContactsList.add(badgeContact);

            }
            badgeContactStore.save(badgeContactsList);

            if (uri.equals(BadgeContactContract.URI_NOTIFY)) {
                resolver.notifyChange(BadgeContactContract.URI, null, false);
            }
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
            DevNexusContentProvider.schedule = (ArrayList<Schedule>) scheduleStore.readAll();
            resolver.notifyChange(ScheduleContract.URI, null, false);
            return 1;
        }
    }

    public static class ScheduleInsert implements Operation<Uri> {

        @Override
        public Uri exec(Gson gson, SQLStore scheduleStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            Schedule calendar = gson.fromJson(values[0].getAsString(ScheduleContract.DATA), Schedule.class);
            scheduleStore.save(calendar);
            schedule = (ArrayList<Schedule>) scheduleStore.readAll();
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

            if (values[0].getAsBoolean(PresentationContract.NOTIFY) != null && values[0].getAsBoolean(PresentationContract.NOTIFY)) {
                resolver.notifyChange(PresentationContract.URI, null, false);
            }
            return PresentationContract.URI;
        }
    }

    private static class PresentationBulkInsert implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            List<Presentation> presentationsList = new ArrayList<>(values.length);
            for (ContentValues value : values) {
                Presentation presentation = gson.fromJson(value.getAsString(PresentationContract.DATA), Presentation.class);
                presentationsList.add(presentation);

            }
            presentationStore.save(presentationsList);
            DevNexusContentProvider.presentations = null;
            resolver.notifyChange(PresentationContract.URI, null, false);
            return values.length;
        }
    }


    public static class BadgeContactInsert implements Operation<Uri> {

        @Override
        public Uri exec(Gson gson, SQLStore badgeContactStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            BadgeContact presentation = gson.fromJson(values[0].getAsString(BadgeContactContract.DATA), BadgeContact.class);
            badgeContactStore.save(presentation);
            if (uri.equals(BadgeContactContract.URI_NOTIFY)) {
                resolver.notifyChange(BadgeContactContract.URI, null);
            }
            return BadgeContactContract.URI;
        }
    }


    private static class PresentationQuery implements Operation<Cursor> {

        @Override
        public SingleColumnJsonArrayList exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {
            if (DevNexusContentProvider.presentations == null) {
                DevNexusContentProvider.presentations = new ArrayList<Presentation>(presentationStore.readAll());
                if (presentations.isEmpty()) {
                    loadTemplate(presentationStore);
                }
            }

            if (selection == null || selection.isEmpty()) {
                return new SingleColumnJsonArrayList(new ArrayList<Presentation>(DevNexusContentProvider.presentations));
            } else {
                String[] queries = selection.split(" && ");
                JSONObject where = new JSONObject();
                for (int i = 0; i < queries.length; i++) {
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

        private void loadTemplate(SQLStore presentationStore) {
            InputStream templateStream = context.getResources().openRawResource(R.raw.presentations);
            String scheduleDataJson = null;
            try {
                scheduleDataJson = IOUtils.toString(templateStream);
            } catch (IOException ignore) {
                Log.e(TAG, ignore.getMessage(), ignore);
            }
            presentations = new ArrayList<>(GSON.fromJson(scheduleDataJson, PresentationResponse.class).presentations);
            presentationStore.reset();
            presentationStore.save(presentations);
        }
    }


    private static class PresentationUpdate implements Operation<Integer> {

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

    private static class PresentationDelete implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore presentationStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (selectionArgs == null || selectionArgs[0] == null) {
                presentationStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                presentationStore.remove(id);
            }
            Presentation schedule = gson.fromJson(values[0].getAsString(PresentationContract.DATA), Presentation.class);
            presentationStore.save(schedule);
            DevNexusContentProvider.presentations = null;
            if (values[0].getAsBoolean(ScheduleContract.NOTIFY) != null && values[0].getAsBoolean(PresentationContract.NOTIFY)) {
                resolver.notifyChange(PresentationContract.URI, null, false);
            }
            return 1;
        }
    }

    private static class BadgeContactDelete implements Operation<Integer> {

        @Override
        public Integer exec(Gson gson, SQLStore badgeContactStore, Uri uri, ContentValues[] values, String selection, String[] selectionArgs) {

            if (selectionArgs == null || selectionArgs[0] == null) {
                badgeContactStore.reset();
            } else {
                Long id = Long.getLong(selectionArgs[0]);
                badgeContactStore.remove(id);
            }
            BadgeContact schedule = gson.fromJson(values[0].getAsString(BadgeContactContract.DATA), BadgeContact.class);
            badgeContactStore.save(schedule);

            if (values[0].getAsBoolean(BadgeContactContract.NOTIFY) != null || uri.equals(BadgeContactContract.URI_NOTIFY)) {
                resolver.notifyChange(BadgeContactContract.URI, null, false);
            }
            return 1;
        }
    }

}
