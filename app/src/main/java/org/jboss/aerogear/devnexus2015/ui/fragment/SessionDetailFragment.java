package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.devnexus.util.GsonUtils;
import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.Speaker;
import org.devnexus.vo.contract.PresentationContract;
import org.devnexus.vo.contract.PreviousYearPresentationContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.TagAdapter;
import org.jboss.aerogear.devnexus2015.ui.view.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by summers on 1/5/15.
 */
public class SessionDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TOOLBAR_TITLE = "SessionDetailFragment.toolbar_title";
    private static final String PRESENTATION_ID = "SessionDetailFragment.presentation_id";
    private static final int DETAIL_LOADER = 0x0100;
    private static final String CONTENT_URI = "SessionDetailFragment.content_uri";
    private static final String EVENT_LABEL = "SessionDetailFragment.event_label";
    private Toolbar toolbar;
    private LinearLayout speakersView;
    private ContentResolver resolver;
    private TextView description;
    private TextView skill;
    private TextView track;
    private TextView slot;
    private Uri contentUri;
    private String eventLabel;
    private HorizontalListView tags;
    private View view;
    private SpeakerSessionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.session_detail_layout, null);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().getString(TOOLBAR_TITLE));
        contentUri = getArguments().getParcelable(CONTENT_URI);
        eventLabel = getArguments().getString(EVENT_LABEL);
        ((MainActivity) getActivity()).attachToolbar(toolbar);
        speakersView = (LinearLayout) view.findViewById(R.id.speakers);

        resolver = getActivity().getContentResolver();
        adapter = (new SpeakerSessionAdapter(new ArrayList<Speaker>(1), getActivity()));
        description = (TextView) view.findViewById(R.id.description);
        track = (TextView) view.findViewById(R.id.track);
        skill = (TextView) view.findViewById(R.id.skill_level);
        slot = (TextView) view.findViewById(R.id.slot);
        tags = (HorizontalListView) view.findViewById(R.id.tags);

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        getLoaderManager().initLoader(DETAIL_LOADER, getArguments(), this);
        return view;
    }

    public static SessionDetailFragment newInstance(String title, int presentationId) {
        Bundle args = new Bundle();
        args.putString(TOOLBAR_TITLE, title);
        args.putParcelable(CONTENT_URI, PresentationContract.URI);
        args.putInt(PRESENTATION_ID, presentationId);
        SessionDetailFragment fragment = new SessionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SessionDetailFragment newInstance(String title, int presentationId, Uri uri, String eventId) {
        Bundle args = new Bundle();
        args.putString(TOOLBAR_TITLE, title);
        args.putParcelable(CONTENT_URI, uri);
        args.putInt(PRESENTATION_ID, presentationId);
        args.putString(EVENT_LABEL, eventId);
        SessionDetailFragment fragment = new SessionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (contentUri.equals(PresentationContract.URI)) {
            return new CursorLoader(getActivity(), contentUri, null, PresentationContract.toQuery(PresentationContract.PRESENTATION_ID), new String[]{"" + args.getInt(PRESENTATION_ID)}, null);
        } else if (contentUri.equals(PreviousYearPresentationContract.URI)) {
            return new CursorLoader(getActivity(), contentUri, null, PresentationContract.toQuery(PresentationContract.PRESENTATION_ID, PreviousYearPresentationContract.EVENT_LABEL), new String[]{"" + args.getInt(PRESENTATION_ID), args.getString(EVENT_LABEL)}, null);
        }
        throw new IllegalStateException("Unsupported content Uri");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        final Presentation presentation = GsonUtils.GSON.fromJson(data.getString(0), Presentation.class);

        description.setText(presentation.description);

        int color = getResources().getColor(R.color.dn_default);

        if (presentation.track != null) {
            color = getResources().getColor(TrackRoomUtil.forTrack(presentation.track.name));
        }

        toolbar.setBackgroundColor(color);
        skill.setText(presentation.skillLevel);
        if (presentation.track != null ) {
            track.setText(presentation.track.name);
            track.setVisibility(View.VISIBLE);
        } else {
            track.setVisibility(View.INVISIBLE);
        }
        //todo slot
        if (presentation.presentationTags.size() > 0) {
            view.findViewById(R.id.tags_label).setVisibility(View.VISIBLE);
            tags.setVisibility(View.VISIBLE);
            tags.setAdapter(new TagAdapter(presentation, getActivity()));
        } else {
            view.findViewById(R.id.tags_label).setVisibility(View.INVISIBLE);
            tags.setVisibility(View.INVISIBLE);
        }

        TextView speakersLabel = ((TextView) view.findViewById(R.id.speakersLabel));
        speakersLabel.setTextColor(color);
        switch (presentation.speakers.size()) {
            case 1:
                speakersLabel.setText("Speaker");
                break;
            default:
                speakersLabel.setText("Speakers");
                break;
        }
        adapter = (new SpeakerSessionAdapter(presentation.speakers, loader.getContext()));

        for (int i = 0; i < adapter.getItemCount(); i++) {
            SpeakerSessionAdapter.ViewHolder holder = adapter.createViewHolder(speakersView, 0);
            adapter.bindViewHolder(holder, i);
            speakersView.addView(holder.speakerView);
        }

        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class SpeakerSessionAdapter extends RecyclerView.Adapter<SpeakerSessionAdapter.ViewHolder> {

        private final List<Speaker> items;
        private final Context mContext;

        public SpeakerSessionAdapter(List<Speaker> speakers, Context context) {
            this.items = speakers;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.speakers_session_detail_layout, viewGroup, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Speaker speaker = getItem(position);
            Picasso.with(mContext).load("http://devnexus.com/s/speakers/" + speaker.id + ".jpg").placeholder(R.drawable.speaker).fit().centerCrop().into((holder).photo);
            holder.bio.setText(speaker.bio);

        }

        private Speaker getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final ImageView photo;
            public final View speakerView;
            public final TextView bio;

            public ViewHolder(View itemView) {
                super(itemView);
                speakerView = itemView;
                photo = (ImageView) itemView.findViewById(R.id.photo);
                bio = (TextView) itemView.findViewById(R.id.bio);
            }
        }

    }
}
