package org.devnexus.util;

import org.devnexus.R;

/**
 * Created by summers on 1/3/14.
 */
public enum TrackRoomUtil {

    AUDITORIUM("Sidney Marcus Auditorium", R.color.KEYNOTES, "Keynotes"),
    ROOM_A302("Room A302", R.color.JAVA, "Java"),
    ROOM_A305("Room A305", R.color.JVM_LANGUAGES, "JVM Languages"),
    ROOM_A312("Room A312", R.color.DATA_INTEGRATION_IOT, "Data, Integration & IoT"),
    ROOM_A313("Room A313", R.color.WEB, "Web"),
    ROOM_A315_316("Room A315-316", R.color.CLOUD_DEVOPS, "Cloud + DevOps"),
    ROOM_A301("Room A301", R.color.MICROSERVICES, "Microservices"),
    ROOM_A314("Room A314", R.color.SECURITY, "Security"),
    ROOM_A311("Room A311", R.color.ARCHITECTURE, "Architecture"),
    WORKSHOPS("Workshop (Full Day)", R.color.WORKSHOP, "Workshop (Full Day)"),
    ROOM_A412("Room A412", R.color.HTML5, "HTML5"),
    ROOM_A411("Room A411", R.color.JAVASCRIPT, "JavaScript"),
    ROOM_A404_405("Room A404-405", R.color.MOBILE, "Mobile"),
    ROOM_A406_407("Room A406-407", R.color.AGILE, "Agile"),
    ROOM_A402_403("Room A402-403", R.color.USER_EXPERIENCE_TOOLS, "User Experience + Tools");

    public final String roomName;
    public final int trackColor;
    public final String trackName;

    private TrackRoomUtil(String roomName, int trackColor, String trackName) {
        this.roomName = roomName;
        this.trackColor = trackColor;
        this.trackName = trackName;
    }

    public static TrackRoomUtil room(String roomName) {
        for (TrackRoomUtil room : TrackRoomUtil.values()) {
            if (room.roomName.equals(roomName)) {
                return room;
            }
        }

        throw new IllegalArgumentException("No such room " + roomName);
    }


    public static int forTrack(String trackName) {
        for (TrackRoomUtil room : TrackRoomUtil.values()) {
            if (room.trackName.toLowerCase().equals(trackName.toLowerCase())) {
                return room.trackColor;
            }
        }

        return R.color.dn_default;
    }


}
