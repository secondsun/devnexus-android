package org.devnexus.sync.simple;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Provides a binder to {@link org.devnexus.sync.simple.SimpleDataAuthenticator}
 */
public class SimpleAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private SimpleDataAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SimpleDataAuthenticator(this);
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