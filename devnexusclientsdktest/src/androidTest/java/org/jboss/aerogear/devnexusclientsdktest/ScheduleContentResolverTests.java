package org.jboss.aerogear.devnexusclientsdktest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.Schedule;
import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.ScheduleContract;
import org.devnexus.vo.contract.ScheduleItemContract;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by summers on 12/29/14.
 */
public class ScheduleContentResolverTests extends InstrumentationTestCase{

    private Context context;
    private ContentResolver contentResolver;
    private Gson gson = GsonUtils.GSON;

    protected void setUp() throws Exception {
        super.setUp();
        context = super.getInstrumentation().getTargetContext();
        contentResolver = context.getContentResolver();
        loadSchedule();
    }

    private void loadSchedule() throws IOException {
        InputStream scheduleStream = context.getResources().openRawResource(R.raw.schedule);
        String scheduleRawJson = IOUtils.toString(scheduleStream);
        Schedule schedule = gson.fromJson(scheduleRawJson, Schedule.class);
        contentResolver.insert(ScheduleContract.URI, ScheduleContract.valueize(schedule));
    }

    public void testScheduleSave() {
        Cursor cursor = contentResolver.query(ScheduleContract.URI, null, null, null, null);
        while(cursor.moveToNext()) {
            String scheduleRawJson = cursor.getString(0);
            Schedule schedule = gson.fromJson(scheduleRawJson, Schedule.class);
            Assert.assertEquals(127, schedule.scheduleItems.size());
        }
        cursor.close();
    }

    public void testScheduleItemEmptyQuery() {
        Cursor cursor = contentResolver.query(ScheduleItemContract.URI, null, null, null, null);
        Assert.assertEquals(127, cursor.getCount());
        cursor.close();
    }

    public void testScheduleItemQueryId() {
        Cursor cursor = contentResolver.query(ScheduleItemContract.URI, null, ScheduleItemContract.toQuery(ScheduleItemContract.PRESENTATION_ID),new String[] { "3994" }, null);
        Assert.assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        String scheduleItemRawJson = cursor.getString(0);
        ScheduleItem scheduleItem = gson.fromJson(scheduleItemRawJson, ScheduleItem.class);
        assertEquals("Down and Dirty with Java EE 7", scheduleItem.presentation.title);
        cursor.close();
    }


}
