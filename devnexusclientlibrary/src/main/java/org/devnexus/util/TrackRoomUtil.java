package org.devnexus.util;

import org.devnexus.R;

/**
 * Created by summers on 1/3/14.
 */
public enum TrackRoomUtil {

    BALLROOM_A("Ballroom A", R.color.Mobile, "Mobile"),
    BALLROOM_B("Ballroom B", R.color.JavaScript, "JavaScript"),
    BALLROOM_C("Ballroom C", R.color.Java, "Java"),
    BALLROOM_D("Ballroom D", R.color.Data_Integration_IoT, "Data, Integration & IoT"),
    BALLROOM_E("Ballroom E", R.color.Agile, "Agile"),
    BALLROOM_F("Ballroom F", R.color.HTML5, "HTML 5"),

    ROOM_102("Room 102", R.color.Cloud_DevOps, "Cloud + DevOps"),
    ROOM_103("Room 103", R.color.Web, "Web"),
    ROOM_104("Room 104", R.color.JVM_Languages_Debugging, "JVM Languages + Debugging"),
    ROOM_105("Room 105", R.color.Microservices_Security, "Microservices + Security"),
    ROOM_106("Room 106", R.color.Workshop, "WorkShops"),
    ROOM_117("Room 117", R.color.Workshop, "WorkShops"),
    ROOM_FUNCTIONAL_PROGRAMMING("Functional Programming Room", R.color.Functional_Programming, "Functional Programming"),
    ROOM_UX("UX Room", R.color.User_Experience_Tools, "User Experience + Tools"),
    JOCKS_AND_JILLS("Jocks and Jills", R.color.Workshop, "Cocktail Hour");

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
            if (room.trackName.equals(trackName)) {
                return room.trackColor;
            }
        }

        return R.color.dn_white;
    }


}
