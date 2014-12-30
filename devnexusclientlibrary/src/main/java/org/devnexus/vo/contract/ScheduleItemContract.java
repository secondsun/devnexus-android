package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Schedule;

/**
 * Created by summers on 2/14/14.
 */
public class ScheduleItemContract {
    public static final Uri URI = Uri.parse("content://org.devnexus.sync/ScheduleItem");
    public static final String _ID = "id";
    public static final String DATA = "DATA";
    public static String NOTIFY = "NOTIFY";
    public static String TITLE = "presentation.title";
    public static String SPEAKER_FNAME = "presentation.speaker.firstName";
    public static String SPEAKER_NAME = "presentation.speaker.lastName";

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

    /**
     * This method will make a query out of the selection arguments.
     *
     * @param selection the selections you wish to to query
     * @return a string formatted $selection && $selection1 (ex presentation.speaker.firstName && presentation.speaker.lastName)
     */
    public static String toQuery(String... selection) {
        StringBuilder builder = new StringBuilder();
        String and = "";
        for ( String key : selection ) {
            builder.append(and);
            builder.append(key.trim());
            and = " && ";
        }

        return builder.toString();
    }
}