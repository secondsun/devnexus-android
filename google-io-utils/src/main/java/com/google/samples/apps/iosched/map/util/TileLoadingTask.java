/*
 * Copyright 2015 Google Inc. All rights reserved.
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
 */

package com.google.samples.apps.iosched.map.util;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.google.samples.apps.iosched.util.MapUtils;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.samples.apps.iosched.util.LogUtils.LOGD;
import static com.google.samples.apps.iosched.util.LogUtils.makeLogTag;

/**
 * Background task that queries the content provider and prepares a list of
 * {@link com.google.android.gms.maps.model.TileOverlay}s
 * for addition to the map.
 * A tile overlay is always tied to a floor in Moscone and is loaded directly from an SVG file.
 * A {@link DiskLruCache} is used to create a {@link CachedTileProvider} for each overlay.
 * <p>Note: The CachedTileProvider <b>must</b> be closed when the encapsulating map is stopped.
 * (See
 * {@link CachedTileProvider#closeCache()}
 * <p>
 * Modified by Summers Pittman 2015
 */
public class TileLoadingTask extends AsyncTaskLoader<List<TileLoadingTask.TileEntry>> {

    private static final String TAG = makeLogTag(TileLoadingTask.class);


    private final float mDPI;
    private final Map<Integer, String> floorTileFileMapping;


    public TileLoadingTask(Context context, float dpi, Map<Integer, String> floorTileFileMapping) {
        super(context);
        mDPI = dpi;
        this.floorTileFileMapping = new HashMap<>(floorTileFileMapping);
    }

    @Override
    protected void onReset() {
        super.onReset();
        this.floorTileFileMapping.clear();

    }

    @Override
    public List<TileEntry> loadInBackground() {
        List<TileEntry> list;

        // Create a TileProvider for each entry in the cursor
        final int count = floorTileFileMapping.size();

        // Initialise the tile cache that is reused for all TileProviders.
        // Note that the cache *MUST* be closed when the encapsulating Fragment is stopped.
        DiskLruCache tileCache = MapUtils.openDiskCache(getContext());

        list = new ArrayList<>(count);

        for (Map.Entry<Integer, String> entry : floorTileFileMapping.entrySet()) {
            final int floor = entry.getKey();
            final String file = entry.getValue();

            File f = MapUtils.getTileFile(getContext().getApplicationContext(), file);
            if (f == null || !f.exists()) {
                if (MapUtils.hasTileAsset(getContext(), file)) {
                    MapUtils.copyTileAsset(getContext(), file);
                } else {
                    break;
                }
            }

            CachedTileProvider provider;
            try {
                SVGTileProvider svgProvider = new SVGTileProvider(f, mDPI);
                // Wrap the SVGTileProvider in a CachedTileProvider for caching on disk.
                provider = new CachedTileProvider(Integer.toString(floor), svgProvider,
                        tileCache);
            } catch (IOException e) {
                LOGD(TAG, "Could not create Tile Provider.");
                break;
            }
            list.add(new TileEntry(floor, provider));
        }


        return list;
    }

    public class TileEntry {

        public CachedTileProvider provider;
        public int floor;

        TileEntry(int floor, CachedTileProvider provider) {
            this.floor = floor;
            this.provider = provider;
        }
    }

}