package org.devnexus.vo.contract;

import android.content.ContentValues;
import android.net.Uri;

import org.devnexus.util.GsonUtils;
import org.devnexus.vo.BadgeContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Contract class for contacts scanned from Badge Entries
 */
public class BadgeContactContract {

    public static final Uri URI = Uri.parse("content://org.devnexus.sync/Badge");
    public static final Uri URI_NOTIFY = Uri.parse("content://org.devnexus.sync/Badge#notify");
    public static final String _ID = "id";
    public static final String DATA = "DATA";
    public static String NOTIFY = "NOTIFY";

    /**
     * This method will turn a schedule into the appropriate ContentValues for the app.
     *
     * @param badgeContact
     * @return
     */
    public static ContentValues valueize(BadgeContact badgeContact) {
        ContentValues values = new ContentValues();
        values.put(DATA, GsonUtils.GSON.toJson(badgeContact));
        return values;
    }

    /**
     * This method will turn a schedule into the appropriate ContentValues for the app.
     *
     * @param badgeContacts
     * @return
     */
    public static ContentValues[] valueize(List<BadgeContact> badgeContacts) {
        ContentValues[] values = new ContentValues[badgeContacts.size()];

        ArrayList<ContentValues> badgeContactValuesList = new ArrayList<>(values.length);
        for (BadgeContact p : badgeContacts) {
            badgeContactValuesList.add(valueize(p));
        }

        values = badgeContactValuesList.toArray(values);

        return values;
    }

    /**
     * This method will make a query out of the selection arguments.
     *
     * @param selection the selections you wish to to query
     * @return a string formatted $selection && $selection1 (ex badgeContacts.speaker.firstName && badgeContacts.speaker.lastName)
     */
    public static String toQuery(String... selection) {
        StringBuilder builder = new StringBuilder();
        String and = "";
        for (String key : selection) {
            builder.append(and);
            builder.append(key.trim());
            and = " && ";
        }

        return builder.toString();
    }
}
