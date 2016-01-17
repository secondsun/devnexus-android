package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by summers on 12/1/13.
 */
public class UserCalendar implements Comparable<UserCalendar>, Serializable {

    @RecordId
    private Long id = -1l;

    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String username;
    public String fixedTitle;
    public Date fromTime;
    public int duration;//inMinutes

    public List<ScheduleItem> items = new ArrayList<>();
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

        if (version != that.version) return false;
        if (duration != that.duration) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null)
            return false;
        if (updatedDate != null ? !updatedDate.equals(that.updatedDate) : that.updatedDate != null)
            return false;
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (fixedTitle != null ? !fixedTitle.equals(that.fixedTitle) : that.fixedTitle != null)
            return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (fixed != null ? !fixed.equals(that.fixed) : that.fixed != null) return false;
        return template != null ? template.equals(that.template) : that.template == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (fixedTitle != null ? fixedTitle.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + duration;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (fixed != null ? fixed.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
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


    public Date getToTime() {
        return new Date(fromTime.getTime() + 60*1000*duration);
    }
}
