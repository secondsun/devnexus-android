package org.jboss.aerogear.devnexus2015;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.jboss.aerogear.devnexus2015.ui.fragment.SetupFragment;


public class MainActivity extends ActionBarActivity {



    public enum BackStackOperation {NONE, ADD, RESET};

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        
        AccountManager accountManager =
                (AccountManager) getSystemService(
                        ACCOUNT_SERVICE);

        accountManager.addAccountExplicitly(SimpleDataAuthenticator.SIMPLE_ACCOUNT, null, null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (((LinearLayout)findViewById(R.id.display_fragment)).getChildCount() == 0) {
            Fragment loading = SetupFragment.newInstance();
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
                drawerLayout.openDrawer(Gravity.START);
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


    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

}
