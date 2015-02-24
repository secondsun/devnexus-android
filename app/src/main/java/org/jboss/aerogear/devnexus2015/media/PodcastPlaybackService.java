package org.jboss.aerogear.devnexus2015.media;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jboss.aerogear.devnexus2015.ui.fragment.PodcastFragment;

import java.io.File;
import java.io.IOException;

/**
 * Created by summers on 2/23/15.
 */
public class PodcastPlaybackService extends IntentService implements MediaPlayer.OnCompletionListener {

    public static final String TITLE_KEY = "PodcastPlaybackServices.title";
    public static final String FILE_NAME = "PodcastPlaybackServices.fileName";
    public static final String REMOTE_URI = "PodcastPlaybackServices.remoteUri";

    private static final String TAG = PodcastDownloadService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0x100;
    public static final String SUCCESS = "PodcastPlaybackServices.SUCCESS";
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;
    private Notification notification;

    public final MediaPlayer mp = new MediaPlayer();
    private String uri;
    private String fileName;
    private String title;
    private boolean stopped = false;
    public PodcastFragment podcastFragment;
    


    public PodcastPlaybackService() {
        super("PodcastPlaybackServices");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        stopped = false;
        title = intent.getStringExtra(TITLE_KEY);
        fileName = intent.getStringExtra(FILE_NAME);
        uri = intent.getStringExtra(REMOTE_URI);

        startNotification();
        try {
            acquireWifiLock();
            acquireWakeLock();

            startPlayback();
            while (!stopped && podcastFragment != null) {
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            sendFailure(e);
            if (wifiLock != null && wifiLock.isHeld()) {
                wifiLock.release();
            }

            if (wakeLock != null && wakeLock.isHeld()) {
                wakeLock.release();
            }

        } finally {

           
        }


    }

    private void startPlayback() {
        File file = getOutputMediaFile(fileName);
        String link;
        if (file.exists()) {
            link = file.getAbsolutePath(); 
        } else {
            link = uri;
        }
        try {
            mp.setDataSource(link);
            mp.prepare();
        } catch (IOException e) {
            Log.d("PodcastPlayer", e.getMessage(), e);
        }
        mp.setOnCompletionListener(this);
        callPlaybackFragment();
        
    }

    private void callPlaybackFragment() {
        if (podcastFragment != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    podcastFragment.beginPlayback();
                }
            });
            
        }
    }

    private void sendFailure(Exception e) {
        Notification errorNotification = new NotificationCompat.Builder(this).setContentText("Playback Failed").setSubText(e.getMessage()).build();
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(-1, errorNotification);
    }

    private void startNotification() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMedia();
    }

    private void stopMedia() {
        if(mp.isPlaying()) {
            mp.pause();
            mp.stop();
        }
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

    }

    private void pauseMedia() {
        if(mp.isPlaying()){
            mp.pause();
        }

    }

    private void playMedia() {
        if(!mp.isPlaying()) {
            mp.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        stopped = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlaybackBinder(this);
    }

    public void stop() {
        stopMedia();
        mp.reset();
        stopped = true;
    }

    public class PlaybackBinder extends Binder {
        public final PodcastPlaybackService service;

        public PlaybackBinder(PodcastPlaybackService podcastPlaybackService) {
            service = podcastPlaybackService;
        }
    }
}
