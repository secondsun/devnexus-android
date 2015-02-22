package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Schedule;

/**
 * Created by summers on 2/14/14.
 */
public class ScheduleContract {
    public static final Uri URI = Uri.parse("content://org.devnexus.sync/Schedule");
    public static final String _ID = "id";
    public static final String DATA = "DATA";
    public static String NOTIFY = "NOTIFY";
    public static String FROM_TIME = "FROM_TIME";

    public static final String[] COLUMNS = {DATA, NOTIFY};

    private static final Gson GSON = GsonUtils.GSON;


    /**
     * This method will turn a schedule into the appropriate ContentValues for the app.
     *
     * @param schedule
     * @return
     */
    public static ContentValues valueize(Schedule schedule) {
        ContentValues values = new ContentValues();
        values.put(DATA, GSON.toJson(schedule));
        return values;
    }
}
