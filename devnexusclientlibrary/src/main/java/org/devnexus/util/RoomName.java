package org.devnexus.util;

import org.devnexus.R;

/**
 * Created by summers on 1/3/14.
 */
public enum RoomName {
    BALLROOM_A("Ballroom A", R.color.Mobile, "Mobile"),
    BALLROOM_B("Ballroom B", R.color.JavaScript, "JavaScript"),
    BALLROOM_C("Ballroom C", R.color.Java, "Java"),
    BALLROOM_D("Ballroom D", R.color.Data_Integration_IoT, "Data Integration & IoT"),
    BALLROOM_E("Ballroom E", R.color.Agile, "Agile"),
    BALLROOM_F("Ballroom F", R.color.HTML5, "HTML 5"),

    ROOM_102("Room 102", R.color.Cloud_DevOps, "Cloud and DevOps"),
    ROOM_103("Room 103", R.color.Web, "Web"),
    ROOM_104("Room 104", R.color.JVM_Languages_Debugging, "JVM Languages & Debugging"),
    ROOM_105("Room 105", R.color.Microservices_Security, "MicroServices and Security"),
    ROOM_106("Room 106", R.color.Workshop, "WorkShops"),
    ROOM_117("Room 117", R.color.Workshop, "WorkShops"),

    JOCKS_AND_JILLS("Jocks and Jills", R.color.Workshop, "Cocktail Hour");

    public final String roomName;
    public final int trackColor;
    public final String trackName;

    private RoomName(String roomName, int trackColor, String trackName) {
        this.roomName = roomName;
        this.trackColor = trackColor;
        this.trackName = trackName;
    }

    public static RoomName room(String roomName) {
        for (RoomName room : RoomName.values()) {
            if (room.roomName.equals(roomName)) {
                return room;
            }
        }

        throw new IllegalArgumentException("No such room " + roomName);
    }

}
