package org.devnexus.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by summers on 11/13/13.
 */
public class ScheduleItemList implements Serializable {
    public int numberOfSessions = 0;
    public int numberOfKeynoteSessions = 0;
    public int numberOfBreakoutSessions = 0;
    public int numberOfSpeakersAssigned = 0;
    public int numberOfUnassignedSessions = 0;
    public int numberOfBreaks = 0;
    public int numberOfTracks = 0;
    public List<Date> days = new ArrayList<Date>();
    public List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

    public ScheduleItemList() {
        days.add(new Date());
        days.add(new Date());
    }

}
