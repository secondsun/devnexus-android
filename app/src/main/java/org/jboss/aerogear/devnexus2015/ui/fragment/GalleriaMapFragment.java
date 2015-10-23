package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 11/13/13.
 */
public class GalleriaMapFragment extends Fragment implements
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener, OnMapReadyCallback, GoogleMap.OnIndoorStateChangeListener {

    private static final LatLng GALLERIA = new LatLng(33.88346, -84.46695);

    private static final LatLng BALLROOM_A = new LatLng(toDec(33, 53.003, 0), toDec(-84, 28.033, 0));
    private static final LatLng BALLROOM_B = new LatLng(toDec(33, 52.996, 0), toDec(-84, 28.030, 0));
    private static final LatLng BALLROOM_C = new LatLng(toDec(33, 52.984, 0), toDec(-84, 28.025, 0));
    private static final LatLng BALLROOM_D = new LatLng(toDec(33, 52.977, 0), toDec(-84, 28.022, 0));
    private static final LatLng BALLROOM_E = new LatLng(toDec(33, 53.008, 0), toDec(-84, 28.020, 0));
    private static final LatLng BALLROOM_F = new LatLng(toDec(33, 52.984, 0), toDec(-84, 28.010, 0));

    private static final LatLng ROOM_102 = new LatLng(toDec(33, 53.069, 0), toDec(-84, 27.973, 0));
    private static final LatLng ROOM_103 = new LatLng(toDec(33, 53.067, 0), toDec(-84, 27.978, 0));
    private static final LatLng ROOM_104 = new LatLng(toDec(33, 53.065, 0), toDec(-84, 27.982, 0));
    private static final LatLng ROOM_105 = new LatLng(toDec(33, 53.063, 0), toDec(-84, 27.988, 0));


    // Initial camera position
    private static final LatLng CAMERA_GALLERIA = new LatLng(33.88346, -84.46695);
    private static final float CAMERA_ZOOM = 17.75f;
    private static final String TAG = GalleriaMapFragment.class.getSimpleName();
    private GoogleMap mMap;
    private static View view;
    private MapFragment mapFragment;

    @Bind(R.id.toolbar) Toolbar toolbar;
    private List<Marker> markers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.galleria_map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        ButterKnife.bind(this, view);

        toolbar.setTitle("Cobb Galleria");
        ((MainActivity) getActivity()).attachToolbar(toolbar);

        return view;
    }

    private void setupMap(boolean resetCamera) {

        mMap = mapFragment.getMap();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = View.inflate(getActivity(), R.layout.map_info_window, null);
                v.setBackgroundResource(TrackRoomUtil.room(marker.getTitle()).trackColor);
                TextView titleText = (TextView) v.findViewById(R.id.title);
                titleText.setBackgroundResource(TrackRoomUtil.room(marker.getTitle()).trackColor);
                titleText.setText(marker.getTitle());
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = View.inflate(getActivity(), R.layout.map_info_window, null);
                v.setBackgroundResource(ResourceUtils.roomCSSToColor(marker.getTitle()));
                TextView titleText = (TextView) v.findViewById(R.id.title);
                titleText.setText(marker.getTitle());
                titleText.setBackgroundResource(ResourceUtils.roomCSSToColor(marker.getTitle()));
                return v;
            }
        });

        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_A).title("Ballroom A")));
        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_B).title("Ballroom B")));
        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_C).title("Ballroom C")));
        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_D).title("Ballroom D")));
        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_E).title("Ballroom E")));
        markers.add(mMap.addMarker(new MarkerOptions().position(BALLROOM_F).title("Ballroom F")));

        markers.add(mMap.addMarker(new MarkerOptions().position(ROOM_102).title("Room 102")));
        markers.add(mMap.addMarker(new MarkerOptions().position(ROOM_103).title("Room 103")));
        markers.add(mMap.addMarker(new MarkerOptions().position(ROOM_104).title("Room 104")));
        markers.add(mMap.addMarker(new MarkerOptions().position(ROOM_105).title("Room 105")));


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnIndoorStateChangeListener(this);
        if (resetCamera) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(
                    CAMERA_GALLERIA, CAMERA_ZOOM)));
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

    private static double toDec(double deg, double min, double sec) {
        double sign = deg < 0 ? -1 : 1;
        deg = deg * sign;

        double result = sign * (deg + min / 60d + sec / 3600d);

        Log.d(TAG, String.format("%f° %2.0f.%3.0f = %.6f", deg, min, sec, result));
        return result;
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
        LatLng camera = mMap.getCameraPosition().target;
        IndoorBuilding building = mMap.getFocusedBuilding();
        if (building == null) {
            for (Marker marker : markers) {
                marker.setVisible(false);
            }
        } else if (nearGalleria(camera)) {
            building.getLevels().get(0).activate();
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
        }
    }

    private boolean nearGalleria(LatLng camera) {
        Location galleria = new Location("");
        galleria.setLatitude(GALLERIA.latitude);
        galleria.setLongitude(GALLERIA.longitude);

        Location building = new Location("");
        building.setLatitude(camera.latitude);
        building.setLongitude(camera.longitude);

        return galleria.distanceTo(building) < 1000;
    }

    @Override
    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {
        LatLng camera = mMap.getCameraPosition().target;

        if (nearGalleria(camera) && indoorBuilding.getActiveLevelIndex() == 0) {
            for (Marker marker : markers) {
                marker.setVisible(true);
            }
        } else {
            for (Marker marker : markers) {
                marker.setVisible(false);
            }
        }
    }
}

