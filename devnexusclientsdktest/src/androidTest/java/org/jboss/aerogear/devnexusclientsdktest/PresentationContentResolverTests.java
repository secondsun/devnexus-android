package org.jboss.aerogear.devnexusclientsdktest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.contract.PresentationContract;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by summers on 1/4/15.
 */
public class PresentationContentResolverTests  extends InstrumentationTestCase {

    private Context context;
    private ContentResolver contentResolver;
    private Gson gson = GsonUtils.GSON;

    protected void setUp() throws Exception {
        super.setUp();
        context = super.getInstrumentation().getTargetContext();
        contentResolver = context.getContentResolver();
        loadPresentations();
    }

    private void loadPresentations() throws IOException {
        InputStream presentationStream = context.getResources().openRawResource(R.raw.presentations);
        String presentationRawJson = IOUtils.toString(presentationStream);
        PresentationResponse presentations = gson.fromJson(presentationRawJson, PresentationResponse.class);
        contentResolver.bulkInsert(PresentationContract.URI, PresentationContract.valueize(presentations.presentations));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        contentResolver.delete(PresentationContract.URI, null, null);
    }

    public void testQueryAllPresentations() {
        Cursor result = contentResolver.query(PresentationContract.URI, null,null,null,null);
        assertEquals(96, result.getCount());
    }

    public void testQueryPresentationByTrack() {
        Cursor result = contentResolver.query(PresentationContract.URI, null,PresentationContract.toQuery(PresentationContract.TRACK), new String[]{"Mobile"} ,null);
        assertEquals(8, result.getCount());
    }

    public void testQueryPresentationById() {
        Cursor result = contentResolver.query(PresentationContract.URI, null,PresentationContract.toQuery(PresentationContract.PRESENTATION_ID), new String[]{"3994"} ,null);
        assertEquals(1, result.getCount());
    }


    public void testPresentationDoesNotSignalDelete() throws InterruptedException {

        final AtomicBoolean called = new AtomicBoolean(false);

        HandlerThread ht = new HandlerThread("bob");
        ht.start();
        contentResolver.registerContentObserver(PresentationContract.URI, false, new ContentObserver(new Handler(ht.getLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                called.getAndSet(true);
                super.onChange(selfChange);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                called.getAndSet(true);
                super.onChange(selfChange, uri);
            }
        });
        contentResolver.delete(PresentationContract.URI, null, null);
        Thread.sleep(5000);
        assertFalse(called.get());

    }

    public void testPresentationDoesSignalDelete() throws InterruptedException {

        final AtomicBoolean called = new AtomicBoolean(false);

        HandlerThread ht = new HandlerThread("bob");
        ht.start();
        contentResolver.registerContentObserver(PresentationContract.URI, false, new ContentObserver(new Handler(ht.getLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                called.getAndSet(true);
                super.onChange(selfChange);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                called.getAndSet(true);
                super.onChange(selfChange, uri);
            }
        });
        contentResolver.delete(PresentationContract.URI_NOTIFY, null, null);
        Thread.sleep(5000);
        assertTrue(called.get());
    }

}
