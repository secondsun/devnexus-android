package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jboss.aerogear.devnexus2015.R;

import java.io.IOException;

/**
 * Created by summers on 1/7/15.
 */
public class PodcastPlayerFragment extends Fragment implements MediaPlayer.OnCompletionListener {
    private final String title;
    private final  String id;
    private final  String link;
    private final  String track;
    private View view;
    MediaPlayer mp = new MediaPlayer();
    
    public PodcastPlayerFragment(String title, String id, String link, String track) {
        this.title = title;
        this.id = id;
        this.link = link;
        this.track = track;
    }

    public static Fragment newInstance(String title, String id, String link, String track) {
        return new PodcastPlayerFragment(title, id, link, track);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.podcast_player, null);
        ((TextView)view.findViewById(R.id.title)).setText(title);

        view.findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMedia();
            }
        });

        view.findViewById(R.id.pause_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseMedia();
            }
        });
        
        return view;
        
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mp.setDataSource(link);    
            mp.prepare();
        } catch (IOException e) {
            Log.d("PodcastPlayer", e.getMessage(), e);
        }
        mp.setOnCompletionListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMedia();
    }

    private void stopMedia() {
        if(mp.isPlaying()) {
            mp.pause();
            mp.stop();
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
        
    }
}
