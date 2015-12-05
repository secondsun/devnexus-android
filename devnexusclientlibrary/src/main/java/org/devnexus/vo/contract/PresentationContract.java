package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 2/14/14.
 */
public class PresentationContract {
    public static final Uri URI = Uri.parse("content://org.devnexus.sync/Presentation");
    public static final Uri URI_NOTIFY = Uri.parse("content://org.devnexus.sync/Presentation#notify");
    public static final String _ID = "id";
    public static final String DATA = "DATA";
    public static String NOTIFY = "NOTIFY";

    public static final String PRESENTATION_ID = "id";
    public static final String TITLE = "title";
    public static final String SPEAKER_FNAME = "presentations.speaker.firstName";
    public static final String SPEAKER_NAME = "presentations.speaker.lastName";
    public static final String TRACK = "track.name";

    public static final String[] COLUMNS = {DATA, NOTIFY};

    private static final Gson GSON = GsonUtils.GSON;


    /**
     * This method will turn a schedule into the appropriate ContentValues for the app.
     *
     * @param presentation
     * @return
     */
    public static ContentValues valueize(Presentation presentation) {
        ContentValues values = new ContentValues();
        values.put(DATA, GSON.toJson(presentation));
        return values;
    }

    /**
     * This method will turn a schedule into the appropriate ContentValues for the app.
     *
     * @param presentations
     * @return
     */
    public static ContentValues[] valueize(List<Presentation> presentations) {
        ContentValues[] values = new ContentValues[presentations.size()];

        ArrayList<ContentValues> presentationValuesList = new ArrayList<>(values.length);
        for (Presentation p : presentations) {
            presentationValuesList.add(valueize(p));
        }

        values = presentationValuesList.toArray(values);

        return values;
    }

    /**
     * This method will make a query out of the selection arguments.
     *
     * @param selection the selections you wish to to query
     * @return a string formatted $selection && $selection1 (ex presentations.speaker.firstName && presentations.speaker.lastName)
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