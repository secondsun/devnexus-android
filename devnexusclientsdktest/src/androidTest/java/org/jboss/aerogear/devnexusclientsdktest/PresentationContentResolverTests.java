package org.jboss.aerogear.devnexusclientsdktest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.test.InstrumentationTestCase;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.PresentationResponse;
import org.devnexus.vo.contract.PresentationContract;

import java.io.IOException;
import java.io.InputStream;

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
        contentResolver.bulkInsert(PresentationContract.URI, PresentationContract.valueize(presentations.presentationList.presentation));
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

}
