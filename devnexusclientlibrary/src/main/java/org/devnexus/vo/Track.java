package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by summers on 1/4/15.
 */
public class Track implements Serializable, Comparable<Track> {
    @RecordId
    public int id;
    public Date createdDate;
    public Date updatedDate;
    public int version;

    public int trackOrder;

    public String name;
    public String cssStyleName;
    public String color;
    public String description;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;

        Track track = (Track) o;

        if (id != track.id) return false;
        if (trackOrder != track.trackOrder) return false;
        if (version != track.version) return false;
        if (color != null ? !color.equals(track.color) : track.color != null) return false;
        if (createdDate != null ? !createdDate.equals(track.createdDate) : track.createdDate != null)
            return false;
        if (cssStyleName != null ? !cssStyleName.equals(track.cssStyleName) : track.cssStyleName != null)
            return false;
        if (description != null ? !description.equals(track.description) : track.description != null)
            return false;
        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        if (updatedDate != null ? !updatedDate.equals(track.updatedDate) : track.updatedDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + trackOrder;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (cssStyleName != null ? cssStyleName.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public int compareTo(Track track) {
        return trackOrder - track.trackOrder;
    }

}
