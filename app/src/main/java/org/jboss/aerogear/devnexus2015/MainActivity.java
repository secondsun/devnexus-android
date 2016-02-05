package org.jboss.aerogear.devnexus2015;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.devnexus.util.AccountUtil;
import org.devnexus.util.StringUtils;
import org.devnexus.vo.BadgeContact;
import org.devnexus.vo.contract.BadgeContactContract;
import org.jboss.aerogear.devnexus2015.ui.fragment.SetupFragment;
import org.jboss.aerogear.devnexus2015.util.AndroidSignInTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    public static final String LAUNCH_SCREEN = "LaunchScreen";
    public static final int LAUNCH_EXPLORE = 0;
    public static final int LAUNCH_BARCODE = 0x4200;
    public static final int RC_SIGN_IN = 1000;

    //When the devnexus token exchange completes, send return action broadcast.
    private static final String RETURN_ACTION = "return_action";

    @Bind(R.id.my_drawer_layout)
    DrawerLayout drawerLayout;
    @Nullable
    @Bind(R.id.one_column)
    View oneColumn;
    @Nullable
    @Bind(R.id.two_column)
    View twoColumn;
    @Nullable
    @Bind(R.id.three_column)
    View threeColumn;
    private int launch = LAUNCH_EXPLORE;
    private int columnCount = 1;
    private GoogleApiClient mAPiClient;
    //Hack to keep the aciton a schedule is asking the account for
    private String scheduleCallbackAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        AccountManager accountManager =
                (AccountManager) getSystemService(
                        ACCOUNT_SERVICE);

        if (twoColumn != null) {
            columnCount = 2;
        } else if (threeColumn != null) {
            columnCount = 3;
        }

        accountManager.addAccountExplicitly(SimpleDataAuthenticator.SIMPLE_ACCOUNT, null, null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (((LinearLayout) findViewById(R.id.display_fragment)).getChildCount() == 0) {
            if (getIntent() != null) {
                launch = getIntent().getIntExtra(LAUNCH_SCREEN, LAUNCH_EXPLORE);
            }
            Fragment loading = SetupFragment.newInstance(launch);
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            tx.add(R.id.display_fragment, loading);
            tx.commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void attachToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    public void switchFragment(Fragment fragment, BackStackOperation operation, String tag) {

        if (getFragmentManager().findFragmentByTag(tag) != null) {
            return;
        }

        FragmentTransaction tx = getFragmentManager().beginTransaction();

        switch (operation) {

            case NONE:
                break;
            case ADD:
                tx.addToBackStack(tag);
                break;
            case RESET:
                while (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStackImmediate();
                }

                break;
        }

        tx.replace(R.id.display_fragment, fragment, tag);

        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    public void launchBarcodeScanner() {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

        startActivityForResult(intent, LAUNCH_BARCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCH_BARCODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Barcode.ContactInfo vCard = barcode.contactInfo;
                    BadgeContact contact = new BadgeContact();
                    if (vCard.emails != null && vCard.emails.length > 0) {
                        contact.setEmail(vCard.emails[0].address);
                    } else {
                        contact.setEmail("");
                    }

                    if (vCard.name != null) {
                        contact.setFirstName(vCard.name.first);
                        contact.setLastName(vCard.name.last);
                    }

                    contact.setOrganization(vCard.organization);
                    contact.setTitle(vCard.title);

                    ContentValues badgeValues = BadgeContactContract.valueize(contact);
                    getContentResolver().insert(BadgeContactContract.URI_NOTIFY, badgeValues);

                }
            }
        } else if (RC_SIGN_IN == requestCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            new AndroidSignInTask(this).execute(account);
            Log.d("SIGN_IN", String.format("%s %s %s %s", account.getDisplayName(), account.getEmail(), account.getId(), account.getServerAuthCode()));
        } else {
            Toast.makeText(this,"Did not sign into Google.  Synchronization is disabled.", Toast.LENGTH_LONG).show();
        }
    }

    public void setDevNexusToken(String token) {
        AccountUtil.setCookie(this, token);
        if (!StringUtils.isEmpty(this.scheduleCallbackAction)) {
            String action = this.scheduleCallbackAction;
            this.scheduleCallbackAction = null;
            sendBroadcast(new Intent(action));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void tokenExchangeError(String error) {
        Log.d("LOGIN", error);
        this.scheduleCallbackAction = null;
        Toast.makeText(this, "Could not connect to DevNexus. Error : " + error, Toast.LENGTH_LONG).show();
        AccountUtil.setCookie(this, null);
    }

    private GoogleApiClient enableGoogleSignIn() {
        if (mAPiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestServerAuthCode(BuildConfig.SERVER_KEY)
                    .requestIdToken(BuildConfig.SERVER_KEY)
                    .requestEmail()
                    .build();
            mAPiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return mAPiClient;
    }

    public void signIntoGoogleAccount(String action) {

        if (!StringUtils.isEmpty(AccountUtil.getCookie(this))) {
            sendBroadcast(new Intent(action));
            return;
        }

        GoogleApiClient apiClient = enableGoogleSignIn();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        signInIntent.putExtra(RETURN_ACTION, action);
        this.scheduleCallbackAction = action;
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public enum BackStackOperation {NONE, ADD, RESET}

}
