package org.jboss.aerogear.devnexus2015.media;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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

import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.fragment.PodcastFragment;

import java.io.File;
import java.io.IOException;

import static android.media.MediaPlayer.MEDIA_ERROR_IO;
import static android.media.MediaPlayer.MEDIA_ERROR_MALFORMED;
import static android.media.MediaPlayer.MEDIA_ERROR_TIMED_OUT;
import static android.media.MediaPlayer.MEDIA_ERROR_UNSUPPORTED;

public class PodcastPlaybackService2 extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {

    public static final String TITLE_KEY = "PodcastPlaybackServices.title";
    public static final String FILE_NAME = "PodcastPlaybackServices.fileName";
    public static final String REMOTE_URI = "PodcastPlaybackServices.remoteUri";

    private static final String TAG = PodcastDownloadService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0x0200;
    public static final String SUCCESS = "PodcastPlaybackServices.SUCCESS";
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;
    private Notification notification;

    private MediaPlayer mp;
    private String uri;
    private String fileName;
    private String title;

    public PodcastFragment podcastFragment;
    private boolean paused = true;
    private boolean prepared = false;
    private int duration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupMediaPlayer();
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void handleIntent(Intent intent) {

        title = intent.getStringExtra(TITLE_KEY);
        fileName = intent.getStringExtra(FILE_NAME);
        uri = intent.getStringExtra(REMOTE_URI);

        if (mp.isPlaying()) {
            cleanUpOldPlayback();
        }

        startPlayback();

    }

    private void cleanUpOldPlayback() {
        closeNotification();
        stopMedia();
        releaseLocks();
    }

    private void releaseLocks() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void closeNotification() {
        if (notification != null) {
            stopForeground(true);
        }
    }

    private void startPlayback() {
        startNotification();
        String link = getMediaLink();
        acquireWifiLockIfNecessary();

        try {
            mp.setDataSource(link);

            mp.prepareAsync();
        } catch (IOException e) {
            Log.d("PodcastPlayer", e.getMessage(), e);
        }



    }

    private void acquireWifiLockIfNecessary() {
        if (!getOutputMediaFile(fileName).exists()) {
            acquireWifiLock();
        }
    }

    private String getMediaLink() {
        File file = getOutputMediaFile(fileName);
        String link;
        if (file.exists()) {
            link = file.getAbsolutePath();
        } else {
            link = uri;
        }
        return link;
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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.LAUNCH_SCREEN, MainActivity.LAUNCH_PODCAST);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(this).setContentText("Playing ").setSubText(title).setContentIntent(pi).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("DevNexus 2015").
                build();
        notification.flags |= NotificationCompat.FLAG_ONGOING_EVENT;
        notification.icon = R.mipmap.ic_launcher;

        startForeground(NOTIFICATION_ID, notification);
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
        stopForeground(true);
    }

    public void pauseMedia() {
        if(mp.isPlaying()){
            mp.pause();
            paused = true;
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.abandonAudioFocus(this);

        }
        closeNotification();
    }

    private void focusPause() {
        if(mp.isPlaying()){
            mp.pause();
        }
    }

    public void playMedia() {
        if(!mp.isPlaying()) {
            mp.start();
            startNotification();
            paused = false;
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

    }


    private void setupMediaPlayer() {
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.setOnCompletionListener(this);
            mp.setOnErrorListener(this);
            mp.setOnCompletionListener(this);
            mp.setOnPreparedListener(this);
        } else {
            mp.reset();
            prepared = false;
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "Could not get Audio Focus");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlaybackBinder(this);
    }

    public void stop() {
        stopMedia();
        mp.reset();
        prepared = false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopForeground(true);
        mp.release();
        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessageBuilder.append("There was a playback error.");
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                errorMessageBuilder.append(" The server stopped responding.");
                break;
            default:
                Log.e(TAG, "There was a playback error with an Unknown What:" +what);
                break;
        }

        switch (extra) {

            case MEDIA_ERROR_IO:
                errorMessageBuilder.append(" There was an IO error playing the file.");
                break;
            case MEDIA_ERROR_MALFORMED:
                errorMessageBuilder.append(" The file is malformed.");
                break;
            case MEDIA_ERROR_UNSUPPORTED:
                errorMessageBuilder.append(" This file is not supported.");
                break;
            case MEDIA_ERROR_TIMED_OUT:
                errorMessageBuilder.append(" The server stopped responding.");
                break;
            default:
                Log.e(TAG, "There was a playback error with an Unknown extra:" + extra);
                break;
        }

        String message = errorMessageBuilder.toString();

        Log.e(TAG, message);
        Log.e(TAG, fileName);
        sendFailure(new RuntimeException(message));
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        paused = false;
        prepared = true;
        callPlaybackFragment();

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (paused) {
                    playMedia();
                }
                mp.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                stopMedia();
                mp.release();
                prepared = false;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                focusPause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mp.isPlaying()) mp.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    public int getDuration() {
        if (isPrepared())
            return mp.getDuration();
        else
            return 0;
    }

    public int getCurrentPosition() {
        if (isPrepared())
            return mp.getCurrentPosition();
        else
            return 0;
    }

    public void seekTo(int progress) {
        if (isPrepared()) {
            mp.seekTo(progress);
        }
    }

    public class PlaybackBinder extends Binder {
        public final PodcastPlaybackService2 service;

        public PlaybackBinder(PodcastPlaybackService2 podcastPlaybackService) {
            service = podcastPlaybackService;
        }
    }

}
