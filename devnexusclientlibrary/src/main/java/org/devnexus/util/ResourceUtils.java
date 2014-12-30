package org.devnexus.util;

import org.devnexus.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by summers on 11/13/13.
 */
public class ResourceUtils {

    private static final Map<String, Integer> CSS_VALUE_MAP = new HashMap<String, Integer>();


    public static int trackCSSToColor(String trackCSS) {
        if (trackCSS == null) {
            return R.color.dn_orange_red;
        }
        String trackId = trackCSS.replace("-", "_");
        if (CSS_VALUE_MAP.get(trackId) == null) {
            for (Field field : R.color.class.getFields()) {
                if (field.getName().toLowerCase().equals(trackId)) {
                    try {
                        CSS_VALUE_MAP.put(trackId, field.getInt(null));
                    } catch (IllegalAccessException e) {
                        return 0;
                    }
                }
            }
        }

        return CSS_VALUE_MAP.get(trackId);

    }

    public static int roomCSSToColor(String roomName) {
        return trackCSSToColor(RoomName.room(roomName).trackName);
    }

}
