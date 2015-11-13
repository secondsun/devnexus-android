package org.jboss.aerogear.devnexus2015.model;

/**
 * This is a utility class which defines the different locations in the GWCC.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jboss.aerogear.devnexus2015.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.aerogear.devnexus2015.model.RoomMetaData.GalleriaFloor;

public final class GWCCLocations {

    private GWCCLocations() {}

    public static final RoomMetaData A301 = new RoomMetaData(GalleriaFloor.THREE, 33.758336, -84.396093, Color.rgb(231, 126, 165));
    public static final RoomMetaData A302 = new RoomMetaData(GalleriaFloor.THREE, 33.758524, -84.395996, Color.rgb(110, 199, 232));
    public static final RoomMetaData A305 = new RoomMetaData(GalleriaFloor.THREE, 33.758520, -84.396205, Color.rgb(166, 196, 231));
    public static final RoomMetaData A307 = new RoomMetaData(GalleriaFloor.THREE, 33.759381, -84.396462, Color.rgb(1, 83, 81));
    public static final RoomMetaData A311 = new RoomMetaData(GalleriaFloor.THREE, 33.759530, -84.395973, Color.rgb(131, 108, 176));
    public static final RoomMetaData A312 = new RoomMetaData(GalleriaFloor.THREE, 33.759422, -84.395979, Color.rgb(142, 199, 65));
    public static final RoomMetaData A313 = new RoomMetaData(GalleriaFloor.THREE, 33.759251, -84.395974, Color.rgb(250, 162, 27));
    public static final RoomMetaData A314 = new RoomMetaData(GalleriaFloor.THREE, 33.759120, -84.395984, Color.rgb(42, 45, 124));
    public static final RoomMetaData A315 = new RoomMetaData(GalleriaFloor.THREE, 33.758997, -84.396017, Color.rgb(139, 34, 70));
    public static final RoomMetaData A316 = new RoomMetaData(GalleriaFloor.THREE, 33.758993, -84.395888, Color.rgb(139, 34, 70));

    public static final RoomMetaData EXHIBIT_AREA = new RoomMetaData(GalleriaFloor.ONE, 33.759765, -84.396876, 0xe5e5e5);
    public static final RoomMetaData SIDNEY_MARCUS_AUDITORIUM = new RoomMetaData(GalleriaFloor.FOUR, 33.758537, -84.396089, 0xe5e5e5);

    public static final RoomMetaData A402 = new RoomMetaData(GalleriaFloor.FOUR, 33.759322, -84.396430, Color.rgb(91, 144, 63));
    public static final RoomMetaData A403 = new RoomMetaData(GalleriaFloor.FOUR, 33.759431, -84.396433, Color.rgb(91, 144, 63));
    public static final RoomMetaData A404 = new RoomMetaData(GalleriaFloor.FOUR, 33.759601, -84.396433, Color.rgb(72, 121, 188));
    public static final RoomMetaData A405 = new RoomMetaData(GalleriaFloor.FOUR, 33.759716, -84.396433, Color.rgb(72, 121, 188));
    public static final RoomMetaData A406 = new RoomMetaData(GalleriaFloor.FOUR, 33.759850, -84.396437, Color.rgb(72, 121, 188));
    public static final RoomMetaData A407 = new RoomMetaData(GalleriaFloor.FOUR, 33.759943, -84.396438, Color.rgb(72, 121, 188));
    public static final RoomMetaData A411 = new RoomMetaData(GalleriaFloor.FOUR, 33.759481, -84.395963, Color.rgb(72, 121, 188));
    public static final RoomMetaData A412 = new RoomMetaData(GalleriaFloor.FOUR, 33.759295, -84.395962, Color.rgb(72, 121, 188));

    private static final Map<LatLng, RoomMetaData> positionLookup = new HashMap<>(21);

    static {
        positionLookup.put(A301.location,A301);
        positionLookup.put(A302.location,A302);
        positionLookup.put(A305.location,A305);
        positionLookup.put(A307.location,A307);
        positionLookup.put(A311.location,A311);
        positionLookup.put(A312.location,A312);
        positionLookup.put(A313.location,A313);
        positionLookup.put(A314.location,A314);
        positionLookup.put(A315.location,A315);
        positionLookup.put(A316.location,A316);

        positionLookup.put(EXHIBIT_AREA.location,EXHIBIT_AREA);
        positionLookup.put(SIDNEY_MARCUS_AUDITORIUM.location,SIDNEY_MARCUS_AUDITORIUM);

        positionLookup.put(A402.location,A402);
        positionLookup.put(A403.location,A403);
        positionLookup.put(A404.location,A404);
        positionLookup.put(A405.location,A405);
        positionLookup.put(A406.location,A406);
        positionLookup.put(A407.location,A407);
        positionLookup.put(A411.location,A411);
        positionLookup.put(A412.location,A412);
        
    }

    /**
     *
     * Gets the marker options for a floor.
     *
     * @param appContext the applicaiton Context.  Used for loading resources
     * @param floorIndex the google maps index of the floor.
     * @return a List of floors
     */
    public static List<MarkerOptions> asOptions(Context appContext, int floorIndex) {
        List<MarkerOptions> optionsList = new ArrayList<>(21);
        GalleriaFloor floor = RoomMetaData.GalleriaFloor.forIndex(floorIndex);
        Resources resources = appContext.getResources();
        switch (floor) {

            case ONE:
                optionsList.add(new MarkerOptions().position(EXHIBIT_AREA.location).title(resources.getString(R.string.exhibit_area)));
                break;
            case THREE:
                optionsList.add(new MarkerOptions().position(A301.location).title(resources.getString(R.string.a301)));
                optionsList.add(new MarkerOptions().position(A302.location).title(resources.getString(R.string.a302)));
                optionsList.add(new MarkerOptions().position(A305.location).title(resources.getString(R.string.a305)));
                optionsList.add(new MarkerOptions().position(A307.location).title(resources.getString(R.string.a307)));
                optionsList.add(new MarkerOptions().position(A311.location).title(resources.getString(R.string.a311)));
                optionsList.add(new MarkerOptions().position(A312.location).title(resources.getString(R.string.a312)));
                optionsList.add(new MarkerOptions().position(A313.location).title(resources.getString(R.string.a313)));
                optionsList.add(new MarkerOptions().position(A314.location).title(resources.getString(R.string.a314)));
                optionsList.add(new MarkerOptions().position(A315.location).title(resources.getString(R.string.a315)));
                optionsList.add(new MarkerOptions().position(A316.location).title(resources.getString(R.string.a316)));
                break;
            case FOUR:
                optionsList.add(new MarkerOptions().position(SIDNEY_MARCUS_AUDITORIUM.location).title(resources.getString(R.string.sidney_marcus_auditorium)));

                optionsList.add(new MarkerOptions().position(A402.location).title(resources.getString(R.string.a402)));
                optionsList.add(new MarkerOptions().position(A403.location).title(resources.getString(R.string.a403)));
                optionsList.add(new MarkerOptions().position(A404.location).title(resources.getString(R.string.a404)));
                optionsList.add(new MarkerOptions().position(A405.location).title(resources.getString(R.string.a405)));
                optionsList.add(new MarkerOptions().position(A406.location).title(resources.getString(R.string.a406)));
                optionsList.add(new MarkerOptions().position(A407.location).title(resources.getString(R.string.a407)));
                optionsList.add(new MarkerOptions().position(A411.location).title(resources.getString(R.string.a411)));
                optionsList.add(new MarkerOptions().position(A412.location).title(resources.getString(R.string.a412)));
                break;
            case TWO:
            case THREE_M:
            case FIVE:
                //do nothing
        }

        return optionsList;
    }

    public static RoomMetaData lookupRoomFromPosition(LatLng position) {
        return positionLookup.get(position);
    }

}
