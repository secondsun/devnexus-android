package org.devnexus.util;

import org.devnexus.R;

/**
 * Created by summers on 1/3/14.
 */
public enum TrackRoomUtil {

    AUDITORIUM("Sidney Marcus Auditorium", R.color.KEYNOTES, R.drawable.keynote, "Keynotes"),
    MISC("Misc", R.color.dn_default, R.drawable.misc, "Misc"),
    ROOM_A302("Room A302", R.color.JAVA, R.drawable.java, "Java"),
    ROOM_A305("Room A305", R.color.JVM_LANGUAGES, R.drawable.jvm_languages, "JVM Languages"),
    ROOM_A312("Room A312", R.color.DATA_INTEGRATION_IOT, R.drawable.data_iot, "Data, Integration & IoT"),
    ROOM_A313("Room A313", R.color.WEB, R.drawable.web, "Web"),
    ROOM_A315_316("Room A315-316", R.color.CLOUD_DEVOPS, R.drawable.cloud, "Cloud + DevOps"),
    ROOM_A301("Room A301", R.color.MICROSERVICES, R.drawable.microservices, "Microservices"),
    ROOM_A314("Room A314", R.color.SECURITY, R.drawable.security, "Security"),
    ROOM_A311("Room A311", R.color.ARCHITECTURE, R.drawable.architecture, "Architecture"),
    WORKSHOPS("Workshop (Full Day)", R.color.WORKSHOP, R.drawable.workshop, "Workshop (Full Day)"),
    ROOM_A412("Room A412", R.color.HTML5, R.drawable.html5, "HTML5"),
    ROOM_A411("Room A411", R.color.JAVASCRIPT, R.drawable.js, "JavaScript"),
    ROOM_A401("Room A401", R.color.NOSQL, R.drawable.nosql, "NoSQL"),
    ROOM_A404_405("Room A404-405", R.color.MOBILE, R.drawable.mobile, "Mobile"),
    ROOM_A406_407("Room A406-407", R.color.AGILE, R.drawable.agile, "Agile"),
    ROOM_A402_403("Room A402-403", R.color.USER_EXPERIENCE_TOOLS, R.drawable.ux, "User Experience + Tools");

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
