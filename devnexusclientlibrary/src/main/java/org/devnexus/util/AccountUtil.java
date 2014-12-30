package org.devnexus.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by summers on 12/9/13.
 */
public class AccountUtil {

    private static final String ACCOUNT_PREFS = "Devnexus.AccountPrefs";
    private static final String HAS_LOGGED_IN = "Devnexus.AccountPrefs.hasLoggedIn";
    private static final String USERNAME = "Devnexus.AccountPres.username";
    private static final String COOKIE = "Devnexus.AccountPres.cookie";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(ACCOUNT_PREFS, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return context.getSharedPreferences(ACCOUNT_PREFS, Context.MODE_PRIVATE).edit();
    }

    public static void setUsername(Context applicationContext, String accountName) {
        edit(applicationContext).putString(USERNAME, accountName).commit();
    }

    public static String getUsername(Context context) {
        return prefs(context).getString(USERNAME, "");
    }

    public static String getCookie(Context context) {
        return prefs(context).getString(COOKIE, "");
    }

    public static void setCookie(Context context, String cookie) {
        edit(context).putString(COOKIE, cookie).commit();
    }

}
