/*
 * Copyright 2015 Dev Nexus. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This class includes code from https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/map/MapFragment.java
 *
 */
package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.samples.apps.iosched.map.util.CachedTileProvider;
import com.google.samples.apps.iosched.map.util.TileLoadingTask;
import com.google.samples.apps.iosched.util.MapUtils;

import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.GWCCLocations;
import org.jboss.aerogear.devnexus2015.model.RoomMetaData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 11/13/13.
 */
public class VenueMapFragment extends Fragment implements
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraChangeListener, OnMapReadyCallback, GoogleMap.OnIndoorStateChangeListener {

    private static final Map<Integer, String> tileMap;
    private static final String ROOM_VIEW_TAG = "Room View Tag";
    private static final int TOKEN_LOADER_TILES = 0x2;
    // Initial camera position
    private static final LatLng CAMERA_START_POSITION = new LatLng(33.759141, -84.396108);
    private static final float CAMERA_ZOOM = 17.75f;
    private static final String TAG = VenueMapFragment.class.getSimpleName();
    private static View view;

    static {
        tileMap = new HashMap<>(6);
        tileMap.put(3, "gwcc_floor_3.svg");
        tileMap.put(1, "gwcc_floor_4.svg");
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    // Tile Providers
    private SparseArray<CachedTileProvider> mTileProviders =
            new SparseArray<>(6);
    private SparseArray<TileOverlay> mTileOverlays =
            new SparseArray<>(6);
    // Markers stored by floor
    private SparseArray<ArrayList<Marker>> mMarkersFloor =
            new SparseArray<>(3);
    // Screen DPI
    private float mDPI = 0;
    private GoogleMap mMap;
    private MapFragment mapFragment;
    private RoomViewFragment dialog;
    /**
     * LoaderCallbacks for the {@link TileLoadingTask} that loads all tile overlays for the map.
     */
    private LoaderManager.LoaderCallbacks<List<TileLoadingTask.TileEntry>> mTileLoader
            = new LoaderManager.LoaderCallbacks<List<TileLoadingTask.TileEntry>>() {
        @Override
        public Loader<List<TileLoadingTask.TileEntry>> onCreateLoader(int id, Bundle args) {
            return new TileLoadingTask(getActivity(), mDPI, tileMap);
        }

        @Override
        public void onLoadFinished(Loader<List<TileLoadingTask.TileEntry>> loader,
                                   List<TileLoadingTask.TileEntry> data) {
            onTilesLoaded(data);
        }

        @Override
        public void onLoaderReset(Loader<List<TileLoadingTask.TileEntry>> loader) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get DPI
        mDPI = getActivity().getResources().getDisplayMetrics().densityDpi / 160f;

    }

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
        MapUtils.clearDiskCache(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            mapFragment.getMapAsync(this);
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.map, mapFragment, TAG);
            tx.commit();
        } else {
            // load all markers
            LoaderManager lm = getLoaderManager();

            // load the tile overlays
            lm.initLoader(TOKEN_LOADER_TILES, null, mTileLoader).forceLoad();

            setupMap(true);
        }
        if (this.dialog != null) {
            this.dialog.getDialog().show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.dialog != null) {
            if (this.dialog.getDialog() != null) {
                if (!this.dialog.getDialog().isShowing()) {
                    this.dialog = null;
                } else {
                    this.dialog.getDialog().hide();
                }
            }
        }

        if (this.mMarkersFloor != null) {
            this.mMarkersFloor.clear();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        closeTileCache();
        MapUtils.clearDiskCache(getActivity());

    }

    /**
     * Closes the caches of all allocated tile providers.
     *
     * @see CachedTileProvider#closeCache()
     */
    private void closeTileCache() {
        for (int i = 0; i < mTileProviders.size(); i++) {
            try {
                mTileProviders.valueAt(i).closeCache();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        this.dialog = RoomViewFragment.newInstance(marker.getTitle());
        dialog.show(getFragmentManager(), ROOM_VIEW_TAG);
        return true;
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

        // load all markers
        LoaderManager lm = getLoaderManager();

        // load the tile overlays
        lm.initLoader(TOKEN_LOADER_TILES, null, mTileLoader).forceLoad();

        setupMap(true);
    }

    @Override
    public void onIndoorBuildingFocused() {

        if (getActivity() == null) {
            return;
        }

        LatLng camera = mMap.getCameraPosition().target;
        IndoorBuilding building = mMap.getFocusedBuilding();
        clearMap();

        if (building == null || !nearVenue(camera)) {
            return;
        }

        int floor = building.getActiveLevelIndex();

        if (mMarkersFloor.get(floor) == null) {
            mMarkersFloor.put(floor, new ArrayList<Marker>(12));//12 is the most markers on any floor
            ArrayList<Marker> list = mMarkersFloor.get(floor);
            for (MarkerOptions marker : GWCCLocations.asOptions(getActivity(), floor)) {
                list.add(mMap.addMarker(marker));
            }
        }

        ArrayList<Marker> markers = mMarkersFloor.get(floor);
        for (Marker marker : markers) {
            marker.setVisible(true);
        }

        // Overlays
        final TileOverlay overlay = mTileOverlays.get(floor);
        if (overlay != null) {
            overlay.setVisible(true);
        }

    }

    private void clearMap() {
        if (mMap != null) {
            for (int floor =0; floor < mMarkersFloor.size();floor++) {
                ArrayList<Marker> markers = mMarkersFloor.valueAt(floor);
                if (markers != null) {
                    for (Marker marker : markers) {
                        marker.setVisible(false);
                    }
                }
            }

            for (int floor =0; floor < mTileOverlays.size();floor++) {
                TileOverlay overlay = mTileOverlays.valueAt(floor);
                if(overlay!=null) {
                    overlay.setVisible(false);
                }
            }

        }


    }

    private boolean nearVenue(LatLng camera) {
        Location galleria = new Location("");
        galleria.setLatitude(CAMERA_START_POSITION.latitude);
        galleria.setLongitude(CAMERA_START_POSITION.longitude);

        Location building = new Location("");
        building.setLatitude(camera.latitude);
        building.setLongitude(camera.longitude);

        return galleria.distanceTo(building) < 2000;
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
        int floor = indoorBuilding.getActiveLevelIndex();
        clearMap();


        if (mMarkersFloor.get(floor) == null) {
            mMarkersFloor.put(floor, new ArrayList<Marker>(12));//12 is the most markers on any floor
            ArrayList<Marker> list = mMarkersFloor.get(floor);
            for (MarkerOptions marker : GWCCLocations.asOptions(getActivity(), floor)) {
                list.add(mMap.addMarker(marker));
            }
        }

        ArrayList<Marker> markers = mMarkersFloor.get(floor);
        for (Marker marker : markers) {
            marker.setVisible(true);
        }

        // Overlays
        final TileOverlay overlay = mTileOverlays.get(floor);
        if (overlay != null) {
            overlay.setVisible(true);
        }
    }

    private void onTilesLoaded(List<TileLoadingTask.TileEntry> list) {
        if (list != null && mMap != null) {
            // Display tiles if they have been loaded, skip them otherwise but display the rest of
            // the map.
            for (TileLoadingTask.TileEntry entry : list) {
                TileOverlayOptions tileOverlay = new TileOverlayOptions()
                        .tileProvider(entry.provider).visible(false).zIndex(500);

                // Store the tile overlay and provider
                mTileProviders.put(entry.floor, entry.provider);
                mTileOverlays.put(entry.floor, mMap.addTileOverlay(tileOverlay));
            }
        }

    }

}

