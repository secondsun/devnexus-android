package org.jboss.aerogear.devnexus2015.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LiveAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private LiveDataAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new LiveDataAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}