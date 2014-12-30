package org.devnexus.vo;

import org.jboss.aerogear.android.RecordId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by summers on 12/1/13.
 */
public class UserCalendar implements Comparable<UserCalendar>, Serializable {

    @RecordId
    private Long id;

    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String username;
    public Date fromTime;
    public ScheduleItem item;
    public Boolean fixed;
    public Boolean template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCalendar that = (UserCalendar) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        if (fixed != that.fixed) return false;
        if (template != that.template) return false;
        if (version != that.version) return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;

        if (item != null ? !item.equals(that.item) : that.item != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + version;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (fixed ? 1 : 0);
        result = 31 * result + (template ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(UserCalendar another) {
        if (equals(another)) {
            return 0;
        } else if (!fromTime.equals(another.fromTime)) {
            return fromTime.compareTo(another.fromTime);
        } else {
            return id.compareTo(another.id);
        }
    }
}
