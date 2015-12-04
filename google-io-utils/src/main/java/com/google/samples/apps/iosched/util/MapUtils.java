/*
 * Copyright 2014 Google Inc. All rights reserved.
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
 * Modified by Summers Pittman 2015
 *
 */
package com.google.samples.apps.iosched.util;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.samples.apps.iosched.util.LogUtils.LOGD;
import static com.google.samples.apps.iosched.util.LogUtils.LOGE;

public class MapUtils {

    private static final String TILE_PATH = "";
    private static final String TAG = LogUtils.makeLogTag(MapUtils.class);

    private static String[] mapTileAssets;

    /**
     * Return a {@link File} pointing to the storage location for map tiles.
     */
    public static File getTileFile(Context context, String filename) {
        File folder = new File(context.getFilesDir(), TILE_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new File(folder, filename);
    }


    private static final int MAX_DISK_CACHE_BYTES = 1024 * 1024 * 2; // 2MB

    public static DiskLruCache openDiskCache(Context c) {
        File cacheDir = new File(c.getCacheDir(), "tiles");
        try {
            return DiskLruCache.open(cacheDir, 1, 3, MAX_DISK_CACHE_BYTES);
        } catch (IOException e) {
            LOGE(TAG, "Couldn't open disk cache.");

        }
        return null;
    }

    public static void clearDiskCache(Context c) {
        DiskLruCache cache = openDiskCache(c);
        if (cache != null) {
            try {
                LOGD(TAG, "Clearing map tile disk cache");
                cache.delete();
                cache.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Returns true if the given tile file exists as a local asset.
     */
    public static boolean hasTileAsset(Context context, String filename) {

        //cache the list of available files
        if (mapTileAssets == null) {
            try {
                mapTileAssets = context.getAssets().list("");
            } catch (IOException e) {
                // no assets
                mapTileAssets = new String[0];
            }
        }

        // search for given filename
        for (String s : mapTileAssets) {
            if (s.equals(filename)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copy the file from the assets to the map tiles directory if it was
     * shipped with the APK.
     */
    public static boolean copyTileAsset(Context context, String filename) {
        if (!hasTileAsset(context, filename)) {
            // file does not exist as asset
            return false;
        }

        // copy file from asset to internal storage
        try {
            InputStream is = context.getAssets().open(filename);
            File f = getTileFile(context, filename);
            FileOutputStream os = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int dataSize;
            while ((dataSize = is.read(buffer)) > 0) {
                os.write(buffer, 0, dataSize);
            }
            os.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }


}