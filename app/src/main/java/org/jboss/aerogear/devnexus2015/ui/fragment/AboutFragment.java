package org.jboss.aerogear.devnexus2015.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.devnexus.util.GsonUtils;
import org.jboss.aerogear.devnexus2015.MainActivity;
import org.jboss.aerogear.devnexus2015.R;
import org.jboss.aerogear.devnexus2015.model.License;
import org.jboss.aerogear.devnexus2015.model.Sponsor;
import org.jboss.aerogear.devnexus2015.ui.adapter.LicenseRecyclerViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.ScheduleItemWithHeaderViewAdapter;
import org.jboss.aerogear.devnexus2015.ui.adapter.SponsorsRecyclerViewAdapter;
import org.jboss.aerogear.devnexus2015.util.CenteringDecoration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 1/10/16.
 */
public class AboutFragment extends Fragment implements LoaderManager.LoaderCallbacks<List> {

    private static final int SPONSOR_LOADER = 0x100;
    private static final int OSS_LOADER = 0x200;
    @Bind(R.id.sponsors_list)
    RecyclerView sponsorsRecycler;
    @Bind(R.id.license_list)
    RecyclerView ossRecycler;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.spinner_nav)
    Spinner spinner;
    @Bind(R.id.sponsor_content)
    View sponsorContent;
    @Bind(R.id.opensource_content)
    View ossContent;
    private int columnCount = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).attachToolbar(toolbar);
        toolbar.setTitle("About DevNexus");

        this.columnCount = ((MainActivity) getActivity()).getColumnCount();
        sponsorsRecycler.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));
        sponsorsRecycler.addItemDecoration(new CenteringDecoration(columnCount, 230, getActivity()));
        ossRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        getLoaderManager().initLoader(SPONSOR_LOADER, new Bundle(), this).forceLoad();
        getLoaderManager().initLoader(OSS_LOADER, new Bundle(), this).forceLoad();
        loadSpinnerNav(spinner);

        return view;
    }

    private void loadSpinnerNav(Spinner spinner) {
        spinner.setAdapter(new AboutNavigationSpinnerAdapter(getActivity()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0 :
                        loadSponsors();
                        break;
                    case 1:
                        loadOSS();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //doNothing
            }
        });
    }

    private void loadSponsors() {
        sponsorContent.setVisibility(View.VISIBLE);
        ossContent.setVisibility(View.GONE);
    }

    private void loadOSS() {
        sponsorContent.setVisibility(View.GONE);
        ossContent.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SPONSOR_LOADER:
                return new AsyncTaskLoader<List>(getActivity()) {
                    @Override
                    public List loadInBackground() {
                        final List sponsors = new ArrayList(30);
                        final InputStream sponsorsStream = getContext().getResources().openRawResource(R.raw.sponsors);
                        try {
                            final String sponsorsJson = IOUtils.toString(sponsorsStream);
                            final JsonElement jsonRootElement = new JsonParser().parse(sponsorsJson);
                            final JsonArray sponsorsJsonArray = jsonRootElement.getAsJsonObject().getAsJsonArray("sponsors");
                            for (int index = 0; index < sponsorsJsonArray.size(); index++) {
                                final JsonElement sponsorObject = sponsorsJsonArray.get(index);
                                final Sponsor sponsor = GsonUtils.GSON.fromJson(sponsorObject, Sponsor.class);
                                sponsors.add(sponsor);
                            }
                        } catch (IOException e) {
                            Log.e("LOAD_SPONSORS", e.getMessage(), e);
                        }

                        return sponsors;
                    }
                };

            case OSS_LOADER:
                return new AsyncTaskLoader<List>(getActivity()) {
                    @Override
                    public List loadInBackground() {
                        final List licenses = new ArrayList(30);
                        final InputStream licensesStream = getContext().getResources().openRawResource(R.raw.licenses);
                        try {
                            final String licensesJson = IOUtils.toString(licensesStream);
                            final JsonElement jsonRootElement = new JsonParser().parse(licensesJson);
                            final JsonArray licensesJsonArray = jsonRootElement.getAsJsonObject().getAsJsonArray("licenses");
                            for (int index = 0; index < licensesJsonArray.size(); index++) {
                                final JsonElement sponsorObject = licensesJsonArray.get(index);
                                final License license = GsonUtils.GSON.fromJson(sponsorObject, License.class);
                                licenses.add(license);
                            }
                        } catch (IOException e) {
                            Log.e("LOAD_LICENSE", e.getMessage(), e);
                        }

                        return licenses;
                    }
                };

            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {
        if (data.get(0) instanceof Sponsor) {
            refreshSponsorData((List<Sponsor>)data);
        } else if (data.get(0) instanceof License) {
            refreshLicenseData((List<License>)data);
        }
    }

    private void refreshSponsorData(List<Sponsor> sponsors) {
        sponsorsRecycler.setAdapter(new SponsorsRecyclerViewAdapter(new ArrayList<Sponsor>(sponsors), getActivity(), columnCount));
        ((GridLayoutManager)sponsorsRecycler.getLayoutManager()).setSpanSizeLookup(((SponsorsRecyclerViewAdapter)sponsorsRecycler.getAdapter()).getSpanSizeLookup());

    }

    private void refreshLicenseData(List<License> licenses) {
        ossRecycler.setAdapter(new LicenseRecyclerViewAdapter(new ArrayList<License>(licenses), getActivity()));

    }

    @Override
    public void onLoaderReset(Loader<List> loader) {

    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
}
