package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by summers on 12/1/13.
 */
public class UserCalendar implements Comparable<UserCalendar>, Serializable {

    @RecordId
    public int id;

    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String fixedTitle;
    public Date fromTime;
    public int duration;//inMinutes

    public Set<ScheduleItem> items = new HashSet<>();
    public Boolean fixed = Boolean.FALSE;
    public String room;
    public String color;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCalendar that = (UserCalendar) o;

        if (duration != that.duration) return false;
        if (fixedTitle != null ? !fixedTitle.equals(that.fixedTitle) : that.fixedTitle != null)
            return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (fixed != null ? !fixed.equals(that.fixed) : that.fixed != null) return false;
        if (room != null ? !room.equals(that.room) : that.room != null) return false;
        return color != null ? color.equals(that.color) : that.color == null;

    }

    @Override
    public int hashCode() {
        int result = fixedTitle != null ? fixedTitle.hashCode() : 0;
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (fixed != null ? fixed.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(UserCalendar another) {
        if (equals(another)) {
            return 0;
        } else if (!fromTime.equals(another.fromTime)) {
            return fromTime.compareTo(another.fromTime);
        } else {
            return Integer.valueOf(id).compareTo(another.id);
        }
    }


    public Date getToTime() {
        return new Date(fromTime.getTime() + 60*1000*duration);
    }
}
