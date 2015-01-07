package org.devnexus.auth;

import android.content.Context;

import org.jboss.aerogear.android.ConfigurationProvider;
import org.jboss.aerogear.android.authentication.AuthenticationConfiguration;
import org.jboss.aerogear.android.authentication.AuthenticationManager;

/**
 * Created by summers on 1/6/15.
 */
public class GooglePlusAuthenticationConfiguration extends AuthenticationConfiguration<GooglePlusAuthenticationConfiguration> {

    static {
        AuthenticationManager.registerConfigurationProvider(GooglePlusAuthenticationConfiguration.class, new ConfigurationProvider<GooglePlusAuthenticationConfiguration>() {
            @Override
            public GooglePlusAuthenticationConfiguration newConfiguration() {
                return new GooglePlusAuthenticationConfiguration();
            }
        });
    }

    private int timeout;
    private String loginEndpoint;
    private String logoutEndpoint;
    private Context applicationContext;

    @Override
    protected GooglePlusAuthenticationModule buildModule() {
        return new GooglePlusAuthenticationModule(getBaseUrl(), this);
    }

    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    public GooglePlusAuthenticationConfiguration loginEndpoint(String loginEndpoint) {
        this.loginEndpoint = loginEndpoint;
        return this;
    }

    public String getLogoutEndpoint() {
        return logoutEndpoint;
    }

    public GooglePlusAuthenticationConfiguration logoutEndpoint(String logoutEndpoint) {
        this.logoutEndpoint = logoutEndpoint;
        return this;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public GooglePlusAuthenticationConfiguration applicationContext(Context applicationContext) {
        this.applicationContext= applicationContext;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public GooglePlusAuthenticationConfiguration timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
