package org.devnexus.util;

import org.devnexus.R;

/**
 * Created by summers on 1/3/14.
 */
public enum TrackRoomUtil {

    AUDITORIUM("Sidney Marcus Auditorium", R.color.KEYNOTES, R.drawable.keynote, "Keynotes"),
    MISC("Misc", R.color.dn_default, R.drawable.misc, "Misc"),
    EXHIBIT_AREA("Exhibit Area", R.color.dn_food_red, R.drawable.cake, "Dessert"),
    JOYSTICK_GAMEBAR("Joystick Gamebar", R.color.dn_food_red, R.drawable.cake, "After-Party"),
    EXHIBIT_HALL_A2("Exhibit Hall A2", R.color.LUNCH, R.drawable.cake, "Lunch"),
    ROOM_A302("Room A302", R.color.MICROSERVICES, R.drawable.microservices, "Microservices"),
    ROOM_A303("Room A303", R.color.R2D2, R.drawable.microsoft, "R2D2"),
    ROOM_A304("Room A304", R.color.C3P0, R.drawable.microsoft, "C3P0"),
    ROOM_A305("Room A305", R.color.CLOUD_DEVOPS, R.drawable.cloud, "Cloud"),
    ROOM_A312("Room A312", R.color.JVM_LANGUAGES, R.drawable.jvm_languages, "Languages"),
    ROOM_A313("Room A313", R.color.TOOLS, R.drawable.tools, "Tools"),
    ROOM_A315("Room A315", R.color.SECURITY, R.drawable.security, "Security"),
    ROOM_A316("Room A316", R.color.PERFORMANCE, R.drawable.performance, "Performance"),
    ROOM_A301("Room A301", R.color.MICROSERVICES, R.drawable.microservices, "Microservices"),
    ROOM_A314("Room A314", R.color.AGILE, R.drawable.agile, "Agile"),
    ROOM_A311("Room A311", R.color.ARCHITECTURE, R.drawable.architecture, "Frameworks"),
    WORKSHOPS("Workshop (Full Day)", R.color.WORKSHOP, R.drawable.workshop, "Workshop (Full Day)"),
    ROOM_A412("Room A412", R.color.ARCHITECTURE, R.drawable.architecture, "Architecture"),
    ROOM_A411("Room A411", R.color.JAVA, R.drawable.java, "Java"),
    ROOM_A401("Room A401", R.color.NOSQL, R.drawable.nosql, "NoSQL"),
    ROOM_A404("Room A404", R.color.JAVASCRIPT, R.drawable.js, "JavaScript"),
    ROOM_A405("Room A405", R.color.WEB, R.drawable.web, "Web"),
    ROOM_A406_407("Room A406-407", R.color.AGILE, R.drawable.agile, "Agile"),
    ROOM_A402("Room A402", R.color.MOBILE, R.drawable.mobile, "Mobile"),
    ROOM_A403("Room A403", R.color.DATASCIENCE, R.drawable.data_iot, "Data Science");

    public final String roomName;
    public final int trackColor;
    public final String trackName;
    public final int iconResource;

    TrackRoomUtil(String roomName, int trackColor, int iconResource, String trackName) {
        this.roomName = roomName;
        this.trackColor = trackColor;
        this.trackName = trackName;
        this.iconResource = iconResource;
    }

    public static int colorForTrack(String trackName) {
        for (TrackRoomUtil room : TrackRoomUtil.values()) {
            if (room.trackName.toLowerCase().equals(trackName.toLowerCase())) {
                return room.trackColor;
            }
        }

        return R.color.dn_default;
    }


    public static int iconForTrack(String trackName) {
        for (TrackRoomUtil room : TrackRoomUtil.values()) {
            if (room.trackName.toLowerCase().equals(trackName.toLowerCase())) {
                return room.iconResource;
            }
        }

        return R.color.dn_default;
    }

}
