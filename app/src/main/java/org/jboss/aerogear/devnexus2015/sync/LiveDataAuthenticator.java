package org.jboss.aerogear.devnexus2015.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;


public class LiveDataAuthenticator extends AbstractAccountAuthenticator {

    public static final String GOOGLE_ACCOUNT_TYPE = "devnexus.org.Account.Google";

    // Simple constructor
    public LiveDataAuthenticator(Context context) {
        super(context);
    }


    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Bundle toReturn = new Bundle();

        return toReturn;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, final Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final Bundle toReturn = new Bundle();

        if (GOOGLE_ACCOUNT_TYPE.equals(authTokenType)) {

        }

        return toReturn;

    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return "Devnexus Calendar";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }


}

