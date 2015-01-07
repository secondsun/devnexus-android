package org.devnexus.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.devnexus.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by summers on 1/7/15.
 */
public class JsonLoader<D> extends AsyncTaskLoader<D>{
    private final Class<D> dataClass;
    private final int resource;
    private final Bundle args;


    public JsonLoader(Class<D> dataClass, int resource, Context context, Bundle args) {
        super(context);
        this.dataClass = dataClass;
        this.resource = resource;
        this.args = args;
    }

    @Override
    public D loadInBackground() {
        try {
            return GsonUtils.GSON.fromJson(IOUtils.toString(getContext().getResources().openRawResource(resource)), dataClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Bundle getArgs(){
        return args;
    }
    
}
