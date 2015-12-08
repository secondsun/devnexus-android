package org.jboss.aerogear.devnexus2015;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.jboss.aerogear.devnexus2015.ui.fragment.SetupFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {



    public enum BackStackOperation {NONE, ADD, RESET};
    public static final String LAUNCH_SCREEN = "LaunchScreen";
    public static final int LAUNCH_EXPLORE = 0;
    public static final int LAUNCH_PODCAST = 0x4200;

    private int launch = LAUNCH_EXPLORE;
    @Bind(R.id.my_drawer_layout) DrawerLayout drawerLayout;
    @Nullable @Bind(R.id.one_column) View oneColumn;
    @Nullable @Bind(R.id.two_column) View twoColumn;
    @Nullable @Bind(R.id.three_column) View threeColumn;

    private int columnCount = 1;

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
        if (((LinearLayout)findViewById(R.id.display_fragment)).getChildCount() == 0) {
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
        setSupportActionBar(toolbar );
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
                while(getFragmentManager().getBackStackEntryCount() > 0) {
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

}
