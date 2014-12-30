package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.UserCalendar;

/**
 * Created by summers on 2/8/14.
 */
public final class UserCalendarContract {

    public static final Uri URI = Uri.parse("content://org.devnexus.sync/UserCalendar");

    public static final String DATA = "DATA";
    public static final Integer DATA_IDX = 0;
    public static final String NOTIFY = "NOTIFY";
    public static final Integer NOTIFY_IDX = 1;

    public static final String[] COLUMNS = {DATA, NOTIFY};

    private static final Gson GSON = GsonUtils.GSON;

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
}
