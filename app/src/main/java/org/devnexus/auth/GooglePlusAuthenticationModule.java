package org.devnexus.auth;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.devnexus.util.AccountUtil;
import org.devnexus.util.Constants;
import org.jboss.aerogear.android.authentication.AbstractAuthenticationModule;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.core.Provider;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.http.HttpProviderFactory;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Created by summers on 12/5/13.
 */
public class GooglePlusAuthenticationModule extends AbstractAuthenticationModule {

    private static final String[] SCOPES = {"https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"};
    private static final String TAG = GooglePlusAuthenticationModule.class.getSimpleName();

    private final URL baseURL;
    private final String loginEndpoint;
    private final String logoutEndpoint;
    private final Context appContext;
    public static final String ACCOUNT_NAME = "GooglePlusAuthenticationModule.AccountName";
    public static final String ACCOUNT_ID = "GooglePlusAuthenticationModule.AccountId";
    protected final Provider<HttpProvider> httpProviderFactory = new HttpProviderFactory();
    private final int timeout;
    private String cookie;
    private boolean isLoggedIn = false;
    private String accountName;
    private String accountId;

    public GooglePlusAuthenticationModule(URL baseURL, GooglePlusAuthenticationConfiguration config) {
        this.baseURL = baseURL;
        this.loginEndpoint = config.getLoginEndpoint();
        this.logoutEndpoint = config.getLogoutEndpoint();
        this.appContext = config.getApplicationContext();
        timeout = config.getTimeout();
    }


    @Override
    public URL getBaseURL() {
        return baseURL;
    }

    @Override
    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    @Override
    public String getLogoutEndpoint() {
        return logoutEndpoint;
    }

    private URL getLoginURL() {
        try {
            return new URL(baseURL + "/" + loginEndpoint);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getEnrollEndpoint() {
        return null;
    }

    @Override
    public void login(final Map<String, String> authMap, final Callback<HeaderAndBody> headerAndBodyCallback) {
        THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    final String accessToken = GoogleAuthUtil.getToken(appContext,
                            authMap.get(ACCOUNT_NAME),
                            Constants.GOOGLE_PLUS_SERVER_SCOPE,
                            null);


                    HttpProvider provider = httpProviderFactory.get(getLoginURL(), timeout);
                    String loginRequest = new JSONObject(String.format("{\"gPlusId\":\"%s\",\"accessToken\":\"%s\"}", authMap.get(ACCOUNT_ID), accessToken)).toString();

                    HeaderAndBody result = provider.post(loginRequest);
                    cookie = result.getHeader("Set-Cookie").toString();
                    AccountUtil.setCookie(appContext, cookie);
                    isLoggedIn = true;
                    accountName = authMap.get(ACCOUNT_NAME);
                    accountId = authMap.get(ACCOUNT_ID);

                    AccountUtil.setUsername(appContext, accountName);


                    headerAndBodyCallback.onSuccess(result);

                } catch (IOException authEx) {
                    Log.e(TAG, authEx.getMessage(), authEx);
                    headerAndBodyCallback.onFailure(authEx);
                    return;
                } catch (UserRecoverableAuthException e) {
                    Log.e(TAG, e.getMessage(), e);
                    headerAndBodyCallback.onFailure(e);
                    //    accessToken = null;
                } catch (GoogleAuthException authEx) {
                    Log.e(TAG, authEx.getMessage(), authEx);
                    headerAndBodyCallback.onFailure(authEx);
                    return;
                } catch (Exception e) {
                    headerAndBodyCallback.onFailure(e);
                }
            }
        });
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public ModuleFields loadModule(URI uri, String s, byte[] bytes) {
        ModuleFields fields = new ModuleFields();
        fields.addHeader("Cookie", cookie);
        return fields;

    }

    @Override
    public boolean handleError(HttpException incomingException) {
        if (incomingException.getStatusCode() == 401 || incomingException.getStatusCode() == 403 ) {
            try {

                final String accessToken = GoogleAuthUtil.getToken(appContext,
                        accountName,
                        Constants.GOOGLE_PLUS_SERVER_SCOPE,
                        null);


                HttpProvider provider = httpProviderFactory.get(getLoginURL(), timeout);
                String loginRequest = new JSONObject(String.format("{\"gPlusId\":\"%s\",\"accessToken\":\"%s\"}", accountId, accessToken)).toString();

                HeaderAndBody result = provider.post(loginRequest);
                cookie = result.getHeader("Set-Cookie").toString();
                AccountUtil.setCookie(appContext, cookie);
                isLoggedIn = true;

            } catch (Exception e) {
                AccountUtil.setCookie(appContext, "");
                AccountUtil.setUsername(appContext, "");

                isLoggedIn = false;
            }
            return isLoggedIn;
        } else {
            return false;
        }
    }
}
