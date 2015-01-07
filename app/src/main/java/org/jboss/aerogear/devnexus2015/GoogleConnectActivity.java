package org.jboss.aerogear.devnexus2015;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.devnexus.util.AccountUtil;
import org.jboss.aerogear.devnexus2015.sync.LiveDataAuthenticator;

public class GoogleConnectActivity extends ActionBarActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final Integer REQUEST_AUTHORIZATION = 8000;
    private static final String TAG = GoogleConnectActivity.class.getSimpleName();

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final int REQUEST_CODE_RESOLVE_GPS = 10000;


    private ProgressDialog mConnectionProgressDialog;
    private GoogleApiClient googleApiClient;
    private ConnectionResult mConnectionResult;
    private AccountAuthenticatorResponse accountResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(new Scope("https://www.googleapis.com/auth/plus.profile.emails.read"))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);

        googleApiClient = builder.build();

        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        if (getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE) != null) {
            accountResponse = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        }

        setContentView(R.layout.sign_in_layout);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, REQUEST_CODE_RESOLVE_ERR).show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !googleApiClient.isConnected()) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    googleApiClient.connect();
                }
            }
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    googleApiClient.connect();
                }
            }
        }
        // Save the result and resolve the connection failure upon a user click.
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            googleApiClient.connect();
        } else if (requestCode == REQUEST_CODE_RESOLVE_GPS && responseCode == RESULT_OK) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        final String accountName = Plus.AccountApi.getAccountName(googleApiClient);
        AccountUtil.setUsername(getApplicationContext(), accountName);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, LiveDataAuthenticator.ACCOUNT_TYPE);

        AccountManager.get(this).addAccountExplicitly(new Account(accountName, LiveDataAuthenticator.ACCOUNT_TYPE), null, null);

        if (accountResponse != null) {
            accountResponse.onResult(result);
        }

        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //??
    }

}
