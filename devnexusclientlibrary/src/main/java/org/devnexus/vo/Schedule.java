package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by summers on 11/13/13.
 */
public class Schedule implements Serializable {

    @RecordId
    private Long id = -1l;

    public String headerTitle = "";
    public int numberOfSessions = 0;
    public int numberOfKeynoteSessions = 0;
    public int numberOfBreakoutSessions = 0;
    public int numberOfSpeakersAssigned = 0;
    public int numberOfUnassignedSessions = 0;
    public int numberOfBreaks = 0;
    public int numberOfTracks = 0;
    public List<Date> days = new ArrayList<Date>();
    public List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

    public Schedule() {
        days.add(new Date());
        days.add(new Date());
    }
    public String tag = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
