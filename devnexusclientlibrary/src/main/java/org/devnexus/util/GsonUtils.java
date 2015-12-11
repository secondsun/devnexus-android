package org.devnexus.util;

import android.content.ContentResolver;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.devnexus.vo.ScheduleItem;
import org.devnexus.vo.contract.ScheduleItemContract;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for getting builders and gsons which work with DevNexus
 */
public final class GsonUtils {
    private GsonUtils() {
    }


    private static final GsonBuilder builder;
    public static final Gson GSON;

    static {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong() + 5 * 60 * 60 * 1000);//Json dates are GMT, translate to EST
            }
        });

        builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime() - 5 * 60 * 60 * 1000);
            }
        });
        GSON = builder.create();
    }

    public static GsonBuilder builder() {
        return builder;
    }
    
    public static Gson gson(final SimpleDateFormat formatter, final ContentResolver resolver) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return formatter.parse(json.getAsString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        builder.registerTypeAdapter(ScheduleItem.class, new JsonDeserializer() {
            public ScheduleItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    Cursor cursor = resolver.query(ScheduleItemContract.URI, null, ScheduleItemContract.toQuery(ScheduleItemContract.PRESENTATION_ID), new String[]{json.getAsString()}, null);
                    cursor.moveToFirst();
                    ScheduleItem calendarItem = GsonUtils.GSON.fromJson(cursor.getString(0), ScheduleItem.class);
                    return calendarItem;
            }
        });

        return builder.create();
    }

}
