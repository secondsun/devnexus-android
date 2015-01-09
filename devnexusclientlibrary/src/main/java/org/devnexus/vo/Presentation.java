package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Presentation implements Serializable, Comparable<Presentation> {

    private static final String KEYNOTE = "Keynote";
    private static final String BREAKOUT = "Breakout";
    private static final String WORKSHOP = "Workshop";
    @RecordId
    public int id;
    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String audioLink;
    public String description;
    public String presentationLink;
    public List<Speaker> speakers = new ArrayList<Speaker>(0);
    public Set<PresentationTag> presentationTags = new HashSet<>(0);
    public String title;
    public String presentationType;
    public String skillLevel;
    public Track track;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Presentation)) return false;

        Presentation that = (Presentation) o;

        if (id != that.id) return false;
        if (version != that.version) return false;
        if (audioLink != null ? !audioLink.equals(that.audioLink) : that.audioLink != null)
            return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (presentationLink != null ? !presentationLink.equals(that.presentationLink) : that.presentationLink != null)
            return false;
        if (presentationType != null ? !presentationType.equals(that.presentationType) : that.presentationType != null)
            return false;
        if (skillLevel != null ? !skillLevel.equals(that.skillLevel) : that.skillLevel != null)
            return false;
        if (speakers != null ? !speakers.equals(that.speakers) : that.speakers != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (track != null ? !track.equals(that.track) : that.track != null) return false;
        if (updatedDate != null ? !updatedDate.equals(that.updatedDate) : that.updatedDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (audioLink != null ? audioLink.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (presentationLink != null ? presentationLink.hashCode() : 0);
        result = 31 * result + (speakers != null ? speakers.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (presentationType != null ? presentationType.hashCode() : 0);
        result = 31 * result + (skillLevel != null ? skillLevel.hashCode() : 0);
        result = 31 * result + (track != null ? track.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Presentation o) {
        if (presentationType == KEYNOTE && o.presentationType == BREAKOUT) {
            return 1;
        } else if (presentationType == BREAKOUT && o.presentationType == KEYNOTE) {
            return -1;
        }
        if (track == null) {
            if (o.track != null) {
                return -1;
            } else {

            }
        } else if ((o.track == null)) {
            return 1;
        } else {
            if (o.track.compareTo(track) == 0) {
                return title.compareTo(o.title);
            } else {
                return track.compareTo(track);
            }
        }
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
