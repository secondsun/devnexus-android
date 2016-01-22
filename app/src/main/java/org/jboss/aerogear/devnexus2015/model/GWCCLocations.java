package org.jboss.aerogear.devnexus2015.model;

/**
 * This is a utility class which defines the different locations in the GWCC.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.util.GWCCMapIconGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.aerogear.devnexus2015.model.RoomMetaData.GalleriaFloor;

public final class GWCCLocations {

    private GWCCLocations() {
    }

    public static final RoomMetaData A301 = new RoomMetaData(GalleriaFloor.THREE, 33.758336, -84.396093, 0xe77ea5);
    public static final RoomMetaData A302 = new RoomMetaData(GalleriaFloor.THREE, 33.758524, -84.395996, 0x6ec7e8);
    public static final RoomMetaData A305 = new RoomMetaData(GalleriaFloor.THREE, 33.758520, -84.396205, 0xa6c4e7);
    public static final RoomMetaData A307 = new RoomMetaData(GalleriaFloor.THREE, 33.759381, -84.396442, 0x6c7070);
    public static final RoomMetaData A311 = new RoomMetaData(GalleriaFloor.THREE, 33.759530, -84.395973, 0x015351);
    public static final RoomMetaData A312 = new RoomMetaData(GalleriaFloor.THREE, 33.759422, -84.395979, 0x337ab7);
    public static final RoomMetaData A313 = new RoomMetaData(GalleriaFloor.THREE, 33.759251, -84.395974, 0xfaa21b);
    public static final RoomMetaData A314 = new RoomMetaData(GalleriaFloor.THREE, 33.759120, -84.395984, 0x2a2d7c);
    public static final RoomMetaData A315 = new RoomMetaData(GalleriaFloor.THREE, 33.758997, -84.395937, 0x8b2246);
    public static final RoomMetaData A316 = new RoomMetaData(GalleriaFloor.THREE, 33.758993, -84.395888, 0x8b2246);


    public static final RoomMetaData EXHIBIT_AREA = new RoomMetaData(GalleriaFloor.ONE, 33.759765, -84.396876, 0xed1e24);
    public static final RoomMetaData SIDNEY_MARCUS_AUDITORIUM = new RoomMetaData(GalleriaFloor.FOUR, 33.758537, -84.396089, 0xedcd1c);


    public static final RoomMetaData A402 = new RoomMetaData(GalleriaFloor.FOUR, 33.759322, -84.396430, 0x5b903f);
    public static final RoomMetaData A403 = new RoomMetaData(GalleriaFloor.FOUR, 33.759431, -84.396433, 0x5b903f);
    public static final RoomMetaData A404 = new RoomMetaData(GalleriaFloor.FOUR, 33.759601, -84.396433, 0x4879bc);
    public static final RoomMetaData A405 = new RoomMetaData(GalleriaFloor.FOUR, 33.759716, -84.396433, 0x4879bc);
    public static final RoomMetaData A406 = new RoomMetaData(GalleriaFloor.FOUR, 33.759850, -84.396437, 0x832381);
    public static final RoomMetaData A407 = new RoomMetaData(GalleriaFloor.FOUR, 33.759943, -84.396438, 0x832381);
    public static final RoomMetaData A411 = new RoomMetaData(GalleriaFloor.FOUR, 33.759481, -84.395963, 0x127e9c);
    public static final RoomMetaData A412 = new RoomMetaData(GalleriaFloor.FOUR, 33.759295, -84.395962, 0x127e9c);

    public static final RoomMetaData EXHIBIT_HALL_A2 = new RoomMetaData(GalleriaFloor.ONE, 33.759765, -84.396876, 0x0BA861);
    public static final RoomMetaData A401 = new RoomMetaData(GalleriaFloor.FOUR, 33.759182, -84.396440, 0x836cb0);

    private static final Map<LatLng, RoomMetaData> positionLookup = new HashMap<>(21);

    static {
        positionLookup.put(A301.location, A301);
        positionLookup.put(A302.location, A302);
        positionLookup.put(A305.location, A305);
        positionLookup.put(A311.location, A311);
        positionLookup.put(A312.location, A312);
        positionLookup.put(A313.location, A313);
        positionLookup.put(A314.location, A314);
        positionLookup.put(A315.location, A315);
        positionLookup.put(A316.location, A316);

        positionLookup.put(EXHIBIT_HALL_A2.location, EXHIBIT_HALL_A2);
        positionLookup.put(EXHIBIT_AREA.location, EXHIBIT_AREA);
        positionLookup.put(SIDNEY_MARCUS_AUDITORIUM.location, SIDNEY_MARCUS_AUDITORIUM);

        positionLookup.put(A401.location, A401);

        positionLookup.put(A402.location, A402);
        positionLookup.put(A403.location, A403);
        positionLookup.put(A404.location, A404);
        positionLookup.put(A405.location, A405);
        positionLookup.put(A406.location, A406);
        positionLookup.put(A407.location, A407);
        positionLookup.put(A411.location, A411);
        positionLookup.put(A412.location, A412);

    }

    /**
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
        MarkerOptions marker;
        BitmapDescriptor icon;
        switch (floor) {

            case ONE:
                marker = new MarkerOptions().position(EXHIBIT_AREA.location).anchor(0.5f, 0.5f);
                marker.icon(BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(EXHIBIT_AREA, resources.getString(R.string.exhibit_area))));

                optionsList.add(marker);
                break;
            case THREE:
                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A301, resources.getString(R.string.a301)));
                optionsList.add(new MarkerOptions().position(A301.location).title(resources.getString(R.string.a301)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A302, resources.getString(R.string.a302)));
                optionsList.add(new MarkerOptions().position(A302.location).title(resources.getString(R.string.a302)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A305, resources.getString(R.string.a305)));
                optionsList.add(new MarkerOptions().position(A305.location).title(resources.getString(R.string.a305)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A307, resources.getString(R.string.a307)));
                optionsList.add(new MarkerOptions().position(A307.location).title(resources.getString(R.string.a307)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A311, resources.getString(R.string.a311)));
                optionsList.add(new MarkerOptions().position(A311.location).title(resources.getString(R.string.a311)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A312, resources.getString(R.string.a312)));
                optionsList.add(new MarkerOptions().position(A312.location).title(resources.getString(R.string.a312)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A313, resources.getString(R.string.a313)));
                optionsList.add(new MarkerOptions().position(A313.location).title(resources.getString(R.string.a313)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A314, resources.getString(R.string.a314)));
                optionsList.add(new MarkerOptions().position(A314.location).title(resources.getString(R.string.a314)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A315, resources.getString(R.string.a315)));
                optionsList.add(new MarkerOptions().position(A315.location).title(resources.getString(R.string.a315)).anchor(0.5f, 0.5f).icon(icon));

                break;
            case FOUR:
                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(SIDNEY_MARCUS_AUDITORIUM, resources.getString(R.string.sidney_marcus_auditorium)));
                optionsList.add(new MarkerOptions().position(SIDNEY_MARCUS_AUDITORIUM.location).title(resources.getString(R.string.sidney_marcus_auditorium)).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A401, resources.getString(R.string.a401)));
                optionsList.add(new MarkerOptions().position(A401.location).title(resources.getString(R.string.a401)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A402, resources.getString(R.string.a402)));
                optionsList.add(new MarkerOptions().position(A402.location).title(resources.getString(R.string.a402)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A403, resources.getString(R.string.a403)));
                optionsList.add(new MarkerOptions().position(A403.location).title(resources.getString(R.string.a403)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A404, resources.getString(R.string.a404)));
                optionsList.add(new MarkerOptions().position(A404.location).title(resources.getString(R.string.a404)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A405, resources.getString(R.string.a405)));
                optionsList.add(new MarkerOptions().position(A405.location).title(resources.getString(R.string.a405)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A406, resources.getString(R.string.a406)));
                optionsList.add(new MarkerOptions().position(A406.location).title(resources.getString(R.string.a406)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A407, resources.getString(R.string.a407)));
                optionsList.add(new MarkerOptions().position(A407.location).title(resources.getString(R.string.a407)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A411, resources.getString(R.string.a411)));
                optionsList.add(new MarkerOptions().position(A411.location).title(resources.getString(R.string.a411)).anchor(0.5f, 0.5f).icon(icon));

                icon = BitmapDescriptorFactory.fromBitmap(new GWCCMapIconGenerator(appContext).getIcon(A412, resources.getString(R.string.a412)));
                optionsList.add(new MarkerOptions().position(A412.location).title(resources.getString(R.string.a412)).anchor(0.5f, 0.5f).icon(icon));
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

    public static boolean roomNameMatchesMarkerName(String roomName, String markerName) {
        if (markerName == null || roomName == null) {
            return false;
        }
        switch (markerName) {
            case "A301":
                return roomName.equals("WS Room A301") || roomName.equals("Room A301");

            case "A302":
                return roomName.equals("WS Room A302") || roomName.equals("Room A302");

            case "A305":
                return roomName.equals("WS Room A305") || roomName.equals("Room A305");

            case "A307":
                return roomName.equals("WS Room A307") || roomName.equals("Room A307");

            case "A311":
                return roomName.equals("WS Room A311") || roomName.equals("Room A311");

            case "A312":
                return roomName.equals("WS Room A312") || roomName.equals("Room A312");

            case "A313":
                return roomName.equals("WS Room A313") || roomName.equals("Room A313");

            case "A314":
                return roomName.equals("WS Room A314") || roomName.equals("Room A314");

            case "A315":
                return roomName.equals("WS Room A315-316") || roomName.equals("Room A315-316");

            case "A316":
                return roomName.equals("WS Room A315-316") || roomName.equals("Room A315-316");

            case "Sidney Marcus \n Auditorium":
                return roomName.equals("Sidney Marcus Auditorium");

            case "A401":
                return roomName.equals("Room A401");

            case "A402":
                return roomName.equals("Room A402-403");

            case "A403":
                return roomName.equals("Room A402-403");

            case "A404":
                return roomName.equals("Room A404-405");

            case "A405":
                return roomName.equals("Room A404-405");

            case "A406":
                return roomName.equals("Room A406-407");

            case "A407":
                return roomName.equals("Room A406-407");

            case "A411":
                return roomName.equals("Room A411");

            case "A412":
                return roomName.equals("Room A412");

        }
        return false;
    }

}
