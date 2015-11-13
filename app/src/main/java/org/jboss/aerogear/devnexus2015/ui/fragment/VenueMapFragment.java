package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.devnexus.util.ResourceUtils;
import org.devnexus.util.TrackRoomUtil;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.GWCCLocations;
import org.jboss.aerogear.devnexus2015.model.RoomMetaData;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 11/13/13.
 */
public class VenueMapFragment extends Fragment implements
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener, OnMapReadyCallback, GoogleMap.OnIndoorStateChangeListener {

    // Initial camera position
    private static final LatLng CAMERA_START_POSITION = new LatLng(33.759141, -84.396108);
    private static final float CAMERA_ZOOM = 17.75f;
    private static final String TAG = VenueMapFragment.class.getSimpleName();
    private static final int START_FLOOR = GWCCLocations.EXHIBIT_AREA.floor.getFloorIndex();
    private GoogleMap mMap;
    private static View view;
    private MapFragment mapFragment;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.venue_map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        ButterKnife.bind(this, view);

        toolbar.setTitle(R.string.venue_name);
        ((MainActivity) getActivity()).attachToolbar(toolbar);

        return view;
    }

    private void setupMap(boolean resetCamera) {

        mMap = mapFragment.getMap();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = View.inflate(getActivity(), R.layout.map_info_window, null);
                RoomMetaData room = GWCCLocations.lookupRoomFromPosition(marker.getPosition());
                v.setBackground(new ColorDrawable(room.color));
                TextView titleText = (TextView) v.findViewById(R.id.title);
                titleText.setBackground(new ColorDrawable(room.color));
                titleText.setText(marker.getTitle());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = View.inflate(getActivity(), R.layout.map_info_window, null);
                RoomMetaData room = GWCCLocations.lookupRoomFromPosition(marker.getPosition());
                v.setBackground(new ColorDrawable(room.color));
                TextView titleText = (TextView) v.findViewById(R.id.title);
                titleText.setBackground(new ColorDrawable(room.color));
                titleText.setText(marker.getTitle());
                return v;
            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnIndoorStateChangeListener(this);
        if (resetCamera) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(
                    CAMERA_START_POSITION, CAMERA_ZOOM)));
        }

        mMap.setIndoorEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(this);
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.map, mapFragment, TAG);
            tx.commit();
        } else {
            setupMap(true);
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        RoomViewFragment.newInstance(marker.getTitle()).show(getFragmentManager(), TAG);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        try {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupMap(true);
    }

    @Override
    public void onIndoorBuildingFocused() {

        if (getActivity() == null) {
            return;
        }

        LatLng camera = mMap.getCameraPosition().target;
        IndoorBuilding building = mMap.getFocusedBuilding();
        mMap.clear();


        if (building == null || !nearVenue(camera)) {
            return;
        }


        for (MarkerOptions marker : GWCCLocations.asOptions(getActivity(), building.getActiveLevelIndex())) {
            mMap.addMarker(marker);
        }
    }

    private boolean nearVenue(LatLng camera) {
        Location galleria = new Location("");
        galleria.setLatitude(CAMERA_START_POSITION.latitude);
        galleria.setLongitude(CAMERA_START_POSITION.longitude);

        Location building = new Location("");
        building.setLatitude(camera.latitude);
        building.setLongitude(camera.longitude);

        return galleria.distanceTo(building) < 1000;
    }

    @Override
    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
        if (getActivity() == null) {
            return;
        }
        LatLng camera = mMap.getCameraPosition().target;
        if (!nearVenue(camera)) {
            return;
        }
        mMap.clear();

        for (MarkerOptions marker : GWCCLocations.asOptions(getActivity(), indoorBuilding.getActiveLevelIndex())) {
            mMap.addMarker(marker);
        }
    }
}

