package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.devnexus.vo.BadgeContact;
import org.devnexus.vo.contract.BadgeContactContract;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.ui.adapter.BadgeContactViewAdapter;
import org.jboss.aerogear.devnexus2015.util.CenteringDecoration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.devnexus.vo.contract.UserCalendarContract.DATE;

/**
 * A fragment for managing, adding, removing, and exporting badge contacts
 */
public class BadgeContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BADGE_LOADER = 0x100;

    @Nullable
    @Bind(R.id.my_recycler_view)
    RecyclerView recycler;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.no_items)
    View emptyView;

    private ArrayList<BadgeContact> contacts = new ArrayList<>();

    private ContentObserver badgeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

        @Override
        public void onChange(boolean selfChange) {
            getLoaderManager().getLoader(BADGE_LOADER).forceLoad();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            getLoaderManager().getLoader(BADGE_LOADER).forceLoad();
        }
    };
    private int columnCount = 1;

    public BadgeContactsFragment() {
        // Required empty public constructor
    }

    public static BadgeContactsFragment newInstance() {
        BadgeContactsFragment fragment = new BadgeContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.badge_contacts, container, false);
        ButterKnife.bind(this, view);


        ((MainActivity) getActivity()).attachToolbar(toolbar);
        toolbar.setTitle("Badge Contacts");
        setHasOptionsMenu(true);
        this.columnCount = ((MainActivity) getActivity()).getColumnCount();

        recycler.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));
        recycler.setAdapter(new BadgeContactViewAdapter(new ArrayList<BadgeContact>(0), columnCount));
        ((GridLayoutManager)recycler.getLayoutManager()).setSpanSizeLookup(((BadgeContactViewAdapter)recycler.getAdapter()).getSpanSizeLookup());
        recycler.addItemDecoration(new CenteringDecoration(columnCount, 230, getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(BADGE_LOADER, new Bundle(), this);


        getActivity().getContentResolver().registerContentObserver(BadgeContactContract.URI, true, badgeObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().getLoader(BADGE_LOADER).forceLoad();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getContentResolver().unregisterContentObserver(badgeObserver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.badge_contacts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                try {
                    StringBuilder builderCsvString = new StringBuilder();
                    builderCsvString.append("\"First Name\", \"Last Name\", \"Company\", \"Title\", \"E-Mail\"\n");
                    for (BadgeContact contact : contacts) {
                        addCsvLine(contact, builderCsvString);
                    }
                    File imagePath = new File(getActivity().getFilesDir(), "csvs");
                    if (!imagePath.exists()) {
                        imagePath.mkdir();
                    }
                    File file = new File(imagePath, "badges.csv");

                    if (file.exists()) {
                        file.delete();
                    }

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.write(builderCsvString.toString(), fos);
                    fos.close();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(getActivity(), "org.devnexus.files", file);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

                    sendIntent.setType("text/csv");
                    startActivity(Intent.createChooser(sendIntent, "Select Destination"));
                } catch (FileNotFoundException e) {
                    Log.e("CREATE_FILE", e.getMessage(), e);
                } catch (IOException e) {
                    Log.e("CREATE_FILE", e.getMessage(), e);
                }
                break;
            case R.id.action_scan:
                PackageManager pm = getActivity().getPackageManager();
                boolean camera;

                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    ((MainActivity) getActivity()).launchBarcodeScanner();
                } else {
                    Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return false;
    }

    private void addCsvLine(BadgeContact contact, StringBuilder builderCsvString) {
        final String firstName = contact.getFirstName().replace("\"", "\"\"");
        final String lastName = contact.getLastName().replace("\"", "\"\"");
        final String email = contact.getEmail().replace("\"", "\"\"");
        final String company = contact.getOrganization().replace("\"", "\"\"");
        final String title = contact.getTitle().replace("\"", "\"\"");
        builderCsvString.append(String.format("\"%s\", \"%s\", \"%s\", \"%s\", \"%s\"\n", firstName, lastName, company, title, email));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args == null) {
            args = Bundle.EMPTY;
        }

        return new CursorLoader(getActivity(), BadgeContactContract.URI, null, DATE, new String[]{}, null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<org.devnexus.vo.BadgeContact> contacts = new ArrayList<>(data.getCount());
        while (data.moveToNext()) {
            BadgeContact calendarItem = GsonUtils.GSON.fromJson(data.getString(0), BadgeContact.class);
            contacts.add(calendarItem);
        }
        Collections.sort(contacts);
        refreshData(contacts);

    }

    private void refreshData(List<BadgeContact> contacts) {
        this.contacts = new ArrayList<>(contacts);
        if (contacts.isEmpty()) {
            recycler.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            ((BadgeContactViewAdapter) recycler.getAdapter()).setContacts(contacts);
            recycler.getAdapter().notifyDataSetChanged();
            progress.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
