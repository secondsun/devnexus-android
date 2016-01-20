package org.jboss.aerogear.devnexus2015.util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jboss.aerogear.devnexus2015.MainActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AndroidSignInTask extends AsyncTask<GoogleSignInAccount, Void, String> {

    private final MainActivity activity;

    public AndroidSignInTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(GoogleSignInAccount... params) {
        try {
            GoogleSignInAccount account = params[0];

            String token = account.getIdToken();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://devnexus.com/s/loginAndroid")
                    .post(RequestBody.create(MediaType.parse("text/json"), "{\"idToken\":\"" + token + "\"}"))
                    .build();

            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            client.setWriteTimeout(30, TimeUnit.SECONDS);
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            Log.e("LOGIN", e.getMessage(), e);
            return "{\"error\":\"" + e.getMessage() + "\"}";
        } finally {

        }
    }

    @Override
    protected void onPostExecute(String s) {
        JsonElement response = new JsonParser().parse(s);
        if (response.getAsJsonObject().has("error")) {
            activity.tokenExchangeError(response.getAsJsonObject().get("error").getAsString());
        } else {
            activity.setDevNexusToken(response.getAsJsonObject().get("token").getAsString());
        }
    }
}
