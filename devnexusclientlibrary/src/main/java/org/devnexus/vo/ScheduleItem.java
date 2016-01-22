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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleItem that = (ScheduleItem) o;

        return presentation != null ? presentation.equals(that.presentation) : that.presentation == null;

    }

    @Override
    public int hashCode() {
        return presentation != null ? presentation.hashCode() : 0;
    }

    @Override
    public int compareTo(ScheduleItem another) {
        int comparison = fromTime.compareTo(another.fromTime);

        if (comparison != 0) {
            return comparison;
        }

        return title.compareTo(another.title);

    }
}
