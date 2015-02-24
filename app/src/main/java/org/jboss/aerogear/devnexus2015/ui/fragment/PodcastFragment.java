package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;

import org.devnexus.util.JsonLoader;
import org.devnexus.vo.Podcast;
import org.devnexus.vo.PodcastList;
import org.devnexus.vo.ScheduleItem;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.media.PodcastPlaybackService;
import org.jboss.aerogear.devnexus2015.ui.adapter.PodcastSpinnerAdaper;
import org.jboss.aerogear.devnexus2015.ui.adapter.PodcastViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemViewAdapter;
import org.jboss.aerogear.devnexus2015.util.PodcastClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by summers on 1/7/15.
 */
public class PodcastFragment extends Fragment implements LoaderManager.LoaderCallbacks<PodcastList>, PodcastClickListener {

    private static final int SCHEDULE_LOADER = 0x0100;
    private static final String TRACK = "PodcastFragment.trackName";
    private RecyclerView recycler;
    private View contentView;
    private ContentResolver resolver;
    private Toolbar toolbar;
    private LinearLayout playbackLayout;
    private ProgressBar progress;
    private ImageButton playPauseButton;
    private ImageButton downloadButton;
    private SeekBar seekBar;
    private PodcastPlaybackService playbackService;
    private final Handler mHandler = new Handler();
    private MediaPlayer mp;
    private Runnable mRunnable;
    private ServiceConnection playbackConnection = new ServiceConnection() {
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playbackService = ((PodcastPlaybackService.PlaybackBinder)service).service;
            playbackService.podcastFragment = PodcastFragment.this;
            mp = playbackService.mp;
            if (mp.isPlaying()) {
                beginPlayback();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playbackService.podcastFragment  = null;
            playbackService = null;
            if (mp.isPlaying()) {
                endPlayback();
            }
            mp = null;
        }
    };
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = contentView = inflater.inflate(R.layout.podcast_layout, null);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        recycler = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        resolver = getActivity().getContentResolver();
        recycler.setAdapter(new ScheduleItemViewAdapter(new ArrayList<ScheduleItem>(1), getActivity(), true));

        playbackLayout = (LinearLayout) contentView.findViewById(R.id.playback_controls);
        progress = (ProgressBar) playbackLayout.findViewById(R.id.progress_spinner);
        playPauseButton = (ImageButton) playbackLayout.findViewById(R.id.playpause_button);
        downloadButton = (ImageButton) playbackLayout.findViewById(R.id.download_button);
        seekBar = (SeekBar) playbackLayout.findViewById(R.id.seekbar);
        
        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        loadSpinnerNav(spinner);
        return view;
    }

    private void loadSpinnerNav(final Spinner spinner) {
        spinner.setAdapter(new PodcastSpinnerAdaper(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PodcastSpinnerAdaper.ITEMS item = (PodcastSpinnerAdaper.ITEMS) spinner.getAdapter().getItem(position);
                Bundle args = new Bundle();
                toolbar.setBackgroundColor(getResources().getColor(item.getRightDrawable()));
                switch (item) {

                    case AGILE:
                    case CLOUD_DEVOPTS:
                    case DATA_INTEGRATION_IOT:
                    case FUNCTIONAL_PROGRAMMING:
                    case HTML_5:
                    case JAVA:
                    case JAVASCRIPT:
                    case JVM_LANGUAGES:
                    case MICROSERVICES_SECURITY:
                    case MOBILE:
                    case USER_EXPERIENCE_AND_TOOLS:
                    case WEB:
                        args.putString(TRACK, getResources().getString(item.getTitleStringResource()));
                        getLoaderManager().restartLoader(SCHEDULE_LOADER, args, PodcastFragment.this).forceLoad();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCHEDULE_LOADER, Bundle.EMPTY, this).forceLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(playbackConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent playbackIntent = new Intent(getActivity(), PodcastPlaybackService.class);
        getActivity().bindService(playbackIntent, playbackConnection, Context.BIND_ABOVE_CLIENT);
    }

    public static Fragment newInstance() {
        return new PodcastFragment();
    }

    @Override
    public Loader<PodcastList> onCreateLoader(int id, Bundle args) {
        return new JsonLoader<PodcastList>(PodcastList.class, R.raw.podcasts, getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<PodcastList> loader, PodcastList data) {
        
        List<Podcast> finalList = new ArrayList<>();
        List<Podcast> podcasts = data.podcasts;
        Bundle args = ((JsonLoader)loader).getArgs();
        if (args.isEmpty()) {
            finalList =podcasts;
        } else {
            for (Podcast podcast : podcasts) {
                if (podcast.track.equals(args.getString(TRACK))) {
                    finalList.add(podcast);
                }
            }
        }
        
        Collections.sort(finalList);
        refreshData(finalList);

    }

    private void refreshData(List<Podcast> presentationList) {
        recycler.setAdapter(new PodcastViewAdapter(presentationList, getActivity(), this));
    }

    @Override
    public void onLoaderReset(Loader<PodcastList> loader) {
        recycler.setAdapter(new PodcastViewAdapter(new ArrayList<Podcast>(1), getActivity(), this));
    }

    @Override
    public void playPodcast(Podcast podcast) {
        initializePlaybackUI();
        loadPodcast(podcast);
    }

    private void loadPodcast(Podcast podcast) {
        if (playbackService != null) {
            playbackService.stop();
        }
        String title = podcast.title;
        String uri = podcast.link;
        String fileName = Uri.parse(uri).getLastPathSegment();
        Intent playbackIntent = new Intent(getActivity(), PodcastPlaybackService.class);
        
        playbackIntent.putExtra(PodcastPlaybackService.TITLE_KEY, title);
        playbackIntent.putExtra(PodcastPlaybackService.REMOTE_URI, uri);
        playbackIntent.putExtra(PodcastPlaybackService.FILE_NAME, fileName);
        
        getActivity().startService(playbackIntent);
    }

    private void initializePlaybackUI(){
        
        progress.setVisibility(View.VISIBLE);
        playbackLayout.setVisibility(View.VISIBLE);
        playPauseButton.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        downloadButton.setVisibility(View.GONE);
        
    }
    
    public void beginPlayback(){
        playbackLayout.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        playPauseButton.setVisibility(View.VISIBLE);
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);        
        seekBar.setVisibility(View.VISIBLE);
        downloadButton.setVisibility(View.GONE);

        
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mp.start();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        seekBar.setMax(mp.getDuration());
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp != null && fromUser){
                    mp.seekTo(progress);
                }
            }
        });

        
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mp != null){
                    int mCurrentPosition = mp.getCurrentPosition() ;
                    seekBar.setProgress(mCurrentPosition);
                }
                if (playbackService != null && mp.isPlaying()) {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };

        mHandler.postDelayed(mRunnable, 1000);
        mp.start();
    }

    private void endPlayback(){
        playbackLayout.setVisibility(View.GONE);
    }
    
    
}
