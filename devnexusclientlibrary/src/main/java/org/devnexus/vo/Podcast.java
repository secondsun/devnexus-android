package org.devnexus.vo;

/**
 * Created by summers on 1/7/15.
 */
public class Podcast implements Comparable<Podcast> {
    public String trackCss,
    track,
    title,
    link,
    id,
    event_label;

    @Override
    public int compareTo(Podcast another) {
        if (track.equals(another.track))
            return title.compareTo(another.title);
        else 
            return track.compareTo(another.track);
    }
}
