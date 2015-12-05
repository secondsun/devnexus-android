package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.UserCalendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by summers on 2/8/14.
 */
public final class UserCalendarContract {

    public static final Uri URI = Uri.parse("content://org.devnexus.sync/UserCalendar");
    public static final List<Date> DATES = Arrays.asList(new Date[]{asDate(Calendar.FEBRUARY, 15, 2016), asDate(Calendar.FEBRUARY, 16, 2016), asDate(Calendar.FEBRUARY, 17, 2016)});

    public static final String DATA = "DATA";
    public static final String ID = "ID";
    public static final Integer DATA_IDX = 0;
    public static final String NOTIFY = "NOTIFY";
    public static final Integer NOTIFY_IDX = 1;

    public static final String[] COLUMNS = {DATA, NOTIFY};

    private static final Gson GSON = GsonUtils.GSON;
    public static final String DATE = "DATE";
    public static final String PRESENTATION_ID = "PRESENTATION_ID";
    public static final String START_TIME = "START_TIME";

    /**
     * This method will turn a calendarItem into the appropriate ContentValues for the app.
     *
     * @param calendarItem
     * @return
     */
    public static ContentValues valueize(UserCalendar calendarItem, boolean notify) {
        ContentValues values = new ContentValues();
        values.put(DATA, GSON.toJson(calendarItem));
        values.put(NOTIFY, notify);
        return values;
    }

    public static ContentValues[] valueize(List<UserCalendar> userCalendarItems, boolean notify) {
        ContentValues[] values = new ContentValues[userCalendarItems.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = valueize(userCalendarItems.get(i), notify);
        }
        return values;
    }

    private static Date asDate(int month, int day, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal.getTime();
    }


}
