package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by summers on 1/16/16.
 */
public class UserScheduleItem implements Serializable {

    @RecordId
    private Long id = -1l;

    public Date createdDate;
    public Date updatedDate;
    public int version;
    public String username;
    public Date fromTime;
    public Long scheduleItemId;

}
