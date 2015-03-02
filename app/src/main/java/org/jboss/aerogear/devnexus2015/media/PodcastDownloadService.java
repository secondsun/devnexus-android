package org.jboss.aerogear.devnexus2015.media;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by summers on 2/23/15.
 */
public class PodcastDownloadService extends IntentService {

    public static final String TITLE_KEY = "PodcastDownloadService.title";
    public static final String FILE_NAME = "PodcastDownloadService.fileName";
    public static final String REMOTE_URI = "PodcastDownloadService.remoteUri";

    private static final String TAG = PodcastDownloadService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0x100;
    public static final String SUCCESS = "PodcastDownloadService.SUCCESS";
    WifiManager.WifiLock wifiLock;
    PowerManager.WakeLock wakeLock;
    private Notification notification;

    public PodcastDownloadService() {
        super("PodcastDownloadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String title = intent.getStringExtra(TITLE_KEY);
        String fileName = intent.getStringExtra(FILE_NAME);
        String uri = intent.getStringExtra(REMOTE_URI);

        startNotification(title);
        try {
            acquireWifiLock();
            acquireWakeLock();

            Uri fileUri = doDownload(uri, fileName);
            sendSuccess(fileUri);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            sendFailure(e);
        } finally {

            if (wifiLock != null && wifiLock.isHeld()) {
                wifiLock.release();
            }

            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }

        }


    }

    private void sendFailure(Exception e) {
        Notification errorNotification = new NotificationCompat.Builder(this).setContentText("Downloading ").setSubText(e.getMessage()).build();
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(-1, errorNotification);
    }

    private void sendSuccess(Uri filePath) {
        Intent successIntent = new Intent();
        successIntent.setAction(SUCCESS);
        successIntent.setData(filePath);
        sendBroadcast(successIntent);
    }


    private Uri doDownload(String uri, String fileName) throws IOException {
        File file;
        FileUtils.copyURLToFile(new URL(uri), file = getOutputMediaFile(fileName), 60000, 60000);
        return Uri.fromFile(file);
    }

    private void startNotification(String title) {
        notification = new NotificationCompat.Builder(this).setContentText("Downloading ").setSubText(title).setProgress(0, 100, true).build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void acquireWakeLock() {
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();
    }

    private void acquireWifiLock() {
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();
    }

    private static File getOutputMediaFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DevNexus_Podcasts");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        File mediaFile;
        
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                fileName);
    
        return mediaFile;
    }

}
