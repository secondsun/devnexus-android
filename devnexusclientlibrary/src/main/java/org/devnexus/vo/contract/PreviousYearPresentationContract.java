package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import com.google.gson.Gson;

import org.devnexus.R;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 2/14/14.
 */
public class PreviousYearPresentationContract {
    public static final Uri URI = Uri.parse("content://org.devnexus.sync/PreviousYear");
    public static final String DATA = "DATA";
    public static String NOTIFY = "NOTIFY";

    public static final String EVENT_LABEL = "event_id";
    public static final String PRESENTATION_ID = "id";

    private static final Gson GSON = GsonUtils.GSON;

    public static enum Events {
        DEVCON2004(R.raw.devnexus2004, "DevCon 2004"),
        DEVCON2005(R.raw.devnexus2005, "DevCon 2005"),
        DEVCON2006(R.raw.devnexus2006, "DevCon 2006"),
        DEVNEXUX2009(R.raw.devnexus2009, "DevNexus 2009"),
        DEVNEXUX2010(R.raw.devnexus2010, "DevNexus 2010"),
        DEVNEXUX2011(R.raw.devnexus2011, "DevNexus 2011"),
        DEVNEXUX2012(R.raw.devnexus2012, "DevNexus 2012"),
        DEVNEXUX2013(R.raw.devnexus2013, "DevNexus 2013"),
        DEVNEXUX2014(R.raw.devnexus2014, "DevNexus 2014");
        
        private final int rawResourceId;
        private final String label;
        private Events(int rawResourceId, String label) {
            this.rawResourceId = rawResourceId;
            this.label = label;
        }
        
        public static Events fromLabel(String label) {
            for (Events event :Events.values()) {
                if (event.label.equals(label)) {
                    return event;
                }
            }
            throw new IllegalArgumentException("Unsupported label " +label);
        }
        
        public String getLabel(){
            return label;
        }
        
        public int getRawResourceId() {
            return rawResourceId;
        }
        
    }

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
