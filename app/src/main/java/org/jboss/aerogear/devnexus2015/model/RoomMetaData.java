package org.jboss.aerogear.devnexus2015.model;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * This class defines a location, floor, and color of a room in the GWCC.
 */
public class RoomMetaData {
    public enum GalleriaFloor {ONE(5), TWO(4), THREE(3), THREE_M(2), FOUR(1), FIVE(0);

        private final int floorIndex;

        private GalleriaFloor(int mapsFloorIndex) {
            this.floorIndex = mapsFloorIndex;
        }

        public int getFloorIndex() {
            return floorIndex;
        }

        /**
         * Turns a Google Maps Floor Index into a Galleria Floor
         *
         * @param floorIndex the google maps index of the floor
         * @return the GalleriaFloor of the floor
         * @throws IllegalArgumentException if floorIndex is not a valid floor Index
         */
        public static GalleriaFloor forIndex(int floorIndex) {
            for (GalleriaFloor floor : values()) {
                if (floor.floorIndex == floorIndex) {
                    return floor;
                }
            }
            throw new IllegalArgumentException("No such floor " + floorIndex);
        }

    }

    public final GalleriaFloor floor;
    public final LatLng location;
    public final int color;
    /**
     *
     * @param floor the floor of a room
     * @param latitude the latitude of a room
     * @param longitude the longitude of a room
     */
    public RoomMetaData(GalleriaFloor floor, double latitude, double longitude, int color) {
        this.floor = floor;
        this.location = new LatLng(latitude, longitude);
        this.color = color | 0xFF000000;
    }

}
