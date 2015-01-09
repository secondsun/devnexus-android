package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by summers on 12/2/13.
 */
public class SyncStats implements Serializable {


    @RecordId
    private Long id = -1l;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Date scheduleExpires = new Date();
    private Date calendarExpires = new Date();

    public Date getScheduleExpires() {
        return scheduleExpires;
    }

    public void setScheduleExpires(Date scheduleExpires) {
        this.scheduleExpires = scheduleExpires;
    }

    public Date getCalendarExpires() {
        return calendarExpires;
    }

    public void setCalendarExpires(Date calendarExpires) {
        this.calendarExpires = calendarExpires;
    }
}
