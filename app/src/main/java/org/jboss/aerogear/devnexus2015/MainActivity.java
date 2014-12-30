package org.jboss.aerogear.devnexus2015;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.devnexus.sync.simple.SimpleDataAuthenticator;
import org.devnexus.vo.contract.ScheduleContract;
import org.jboss.aerogear.devnexus2015.ui.ScheduleFragment;
import org.jboss.aerogear.devnexus2015.ui.SetupFragment;


public class MainActivity extends ActionBarActivity {


    private ContentObserver scheduleObersever;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scheduleObersever = new ContentObserver(new Handler(getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                Fragment schedule = ScheduleFragment.newInstance();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.display_fragment, schedule);
                tx.commit();
            }
        };

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
        getContentResolver().registerContentObserver(ScheduleContract.URI, false, scheduleObersever);
        Fragment loading = SetupFragment.newInstance();
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.add(R.id.display_fragment, loading);
        tx.commit();

    }

    @Override
    protected void onStop() {
        super.onStop();
        getContentResolver().unregisterContentObserver(scheduleObersever);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void attachToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar );
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

}
