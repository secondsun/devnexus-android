package org.devnexus.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by summers on 11/13/13.
 */
public class ScheduleItem implements Serializable, Comparable<ScheduleItem> {
    public int id;
    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String scheduleItemType;
    public String title;
    public Date fromTime;
    public Date toTime;
    public Room room;
    public Presentation presentation;
    public int rowspan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleItem that = (ScheduleItem) o;

        if (id != that.id) return false;
        if (rowspan != that.rowspan) return false;
        if (version != that.version) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null)
            return false;
        if (fromTime != null ? !fromTime.equals(that.fromTime) : that.fromTime != null)
            return false;
        if (presentation != null ? !presentation.equals(that.presentation) : that.presentation != null)
            return false;
        if (room != null ? !room.equals(that.room) : that.room != null) return false;
        if (scheduleItemType != null ? !scheduleItemType.equals(that.scheduleItemType) : that.scheduleItemType != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (toTime != null ? !toTime.equals(that.toTime) : that.toTime != null) return false;
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
        result = 31 * result + (scheduleItemType != null ? scheduleItemType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (fromTime != null ? fromTime.hashCode() : 0);
        result = 31 * result + (toTime != null ? toTime.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (presentation != null ? presentation.hashCode() : 0);
        result = 31 * result + rowspan;
        return result;
    }

    @Override
    public int compareTo(ScheduleItem another) {
        int comparison = fromTime.compareTo(another.fromTime);

        if (comparison == 0) {
            return comparison;
        }

        return title.compareTo(another.title);

    }
}
