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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.devnexus.util.GsonUtils;
import org.devnexus.util.TrackRoomUtil;
import org.devnexus.vo.Presentation;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.Speaker;
import org.devnexus.vo.UserCalendar;
import org.devnexus.vo.contract.PresentationContract;
import org.devnexus.vo.contract.ScheduleItemContract;
import org.devnexus.vo.contract.UserCalendarContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.TagAdapter;
import org.jboss.aerogear.devnexus2015.ui.view.HorizontalListView;
import org.jboss.aerogear.devnexus2015.util.ColorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 1/5/15.
 */
public class SessionDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TOOLBAR_TITLE = "SessionDetailFragment.toolbar_title";
    private static final String PRESENTATION_ID = "SessionDetailFragment.presentation_id";
    private static final int DETAIL_LOADER = 0x0100;
    private static final int CALENDAR_LOADER = 0x0200;
    private static final String CONTENT_URI = "SessionDetailFragment.content_uri";
    private static final String EVENT_LABEL = "SessionDetailFragment.event_label";
    private ContentResolver resolver;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.speakers) LinearLayout speakersView;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.skill_level) TextView skill;
    @Bind(R.id.track) TextView track;
    @Bind(R.id.slot) TextView slot;
    @Bind(R.id.room) TextView room;
    @Bind(R.id.tags) HorizontalListView tags;

    private Uri contentUri;

    private View view;
    private SpeakerSessionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.session_detail_layout, null);
        view.setVisibility(View.INVISIBLE);
        ButterKnife.bind(this, view);


        toolbar.setTitle(getArguments().getString(TOOLBAR_TITLE));
        contentUri = getArguments().getParcelable(CONTENT_URI);

        ((MainActivity) getActivity()).attachToolbar(toolbar);


        resolver = getActivity().getContentResolver();
        adapter = (new SpeakerSessionAdapter(new ArrayList<Speaker>(1), getActivity()));


        getLoaderManager().initLoader(DETAIL_LOADER, getArguments(), this);
        if (contentUri.equals(PresentationContract.URI)) {
            getLoaderManager().initLoader(CALENDAR_LOADER, getArguments(), this);
        }
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

        switch (id) {
            case CALENDAR_LOADER:
                return new CursorLoader(getActivity(), UserCalendarContract.URI, null, UserCalendarContract.PRESENTATION_ID, new String[]{"" + args.getInt(PRESENTATION_ID)}, null);
            case DETAIL_LOADER:
                if (contentUri.equals(PresentationContract.URI)) {
                    return new CursorLoader(getActivity(), ScheduleItemContract.URI, null, ScheduleItemContract.toQuery(ScheduleItemContract.PRESENTATION_ID), new String[]{"" + args.getInt(PRESENTATION_ID)}, null);
                }
                throw new IllegalStateException("Unsupported content Uri");
            default:
                throw new IllegalStateException("Unsupported loader");
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final Button button = (Button) view.findViewById(R.id.add_session);
        switch (loader.getId()) {
            case CALENDAR_LOADER:

                if ( data.getCount() == 0 ) {
                    button.setVisibility(View.VISIBLE);
                    button.setText("Add to my schedule");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Cursor scheduleItemCursor = resolver.query(ScheduleItemContract.URI, null, ScheduleItemContract.PRESENTATION_ID, new String[]{getArguments().getInt(PRESENTATION_ID) + ""}, null);
                            scheduleItemCursor.moveToFirst();
                            ScheduleItem item = GsonUtils.GSON.fromJson(scheduleItemCursor.getString(0), ScheduleItem.class);
                            scheduleItemCursor.close();

                            Cursor userItemCursor = resolver.query(UserCalendarContract.URI, null, UserCalendarContract.START_TIME, new String[]{item.fromTime.getTime() + ""}, null);
                            userItemCursor.moveToFirst();
                            UserCalendar userItem = GsonUtils.GSON.fromJson(userItemCursor.getString(0), UserCalendar.class);
                            userItemCursor.close();

                            Presentation presentation = item.presentation;

                            int id = item.id;
                            item = new ScheduleItem();
                            item.id = id;
                            item.presentation = presentation;

                            userItem.items.add( item );

                            resolver.update(UserCalendarContract.URI, UserCalendarContract.valueize(userItem, true), UserCalendarContract.ID, new String[]{userItem.getId() + ""});
                            button.setText("Scheduled!");
                        }
                    });

                } else {
                    data.moveToFirst();
                    final UserCalendar userCalendarItem = GsonUtils.GSON.fromJson(data.getString(0), UserCalendar.class);
                    if (userCalendarItem.fixed) {
                        button.setVisibility(View.GONE);
                    } else {
                        button.setVisibility(View.VISIBLE);
                        button.setText("Remove from my schedule");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Presentation presentation = null;
                                Cursor scheduleItemCursor = resolver.query(ScheduleItemContract.URI, null, ScheduleItemContract.PRESENTATION_ID, new String[]{getArguments().getInt(PRESENTATION_ID) + ""}, null);
                                scheduleItemCursor.moveToFirst();
                                ScheduleItem item = GsonUtils.GSON.fromJson(scheduleItemCursor.getString(0), ScheduleItem.class);
                                scheduleItemCursor.close();

                                presentation = item.presentation;

                                int id = item.id;
                                item = new ScheduleItem();
                                item.id = id;
                                item.presentation = presentation;
                                userCalendarItem.items.remove( item );
                                resolver.update(UserCalendarContract.URI, UserCalendarContract.valueize(userCalendarItem, true), UserCalendarContract.ID, new String[]{userCalendarItem.getId() + ""});
                                button.setText("Removed!");
                            }
                        });
                    }
                }
                data.close();
                return;
            case DETAIL_LOADER:
                data.moveToFirst();
                view.setVisibility(View.VISIBLE);
                final Presentation presentation;
                final ScheduleItem item;
                if (contentUri.equals(PresentationContract.URI)) {
                    item = GsonUtils.GSON.fromJson(data.getString(0), ScheduleItem.class);
                    presentation = item.presentation;
                    ((TextView)view.findViewById(R.id.date)).setText(new SimpleDateFormat("MMM dd hh:mm a").format(item.fromTime));
                } else {
                    item = null;
                    presentation = GsonUtils.GSON.fromJson(data.getString(0), Presentation.class);;
                    button.setVisibility(View.GONE);
                }


                description.setText(presentation.description);

                int color = getResources().getColor(R.color.dn_default);

                if (presentation.track != null) {
                    color = getResources().getColor(TrackRoomUtil.colorForTrack(presentation.track.name));
                }

                int textColor = ColorUtils.getTextColor(getActivity(), color);

                button.setBackgroundColor(color);
                button.setTextColor(textColor);

                toolbar.setBackgroundColor(color);
                toolbar.setNavigationIcon(ColorUtils.getDrawerDrawable(color));
                toolbar.setTitleTextColor(textColor);
                skill.setText(presentation.skillLevel);
                if (presentation.track != null ) {
                    track.setText(presentation.track.name);
                    if (item.room != null) {
                        room.setText(item.room.name);
                    } else {
                        room.setText("null");
                    }
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
                return;
            default:
                return;
        }


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
            Picasso.with(mContext).load("https://devnexus.com/s/speakers/" + speaker.id + ".jpg").placeholder(R.drawable.speaker).fit().centerCrop().into((holder).photo);
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
                photo = (ImageView) itemView.findViewById(R.id.icon);
                bio = (TextView) itemView.findViewById(R.id.bio);
            }
        }

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
