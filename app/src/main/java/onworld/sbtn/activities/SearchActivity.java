package onworld.sbtn.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.PagerAdapterSearch;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.Search;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.URL;

public class SearchActivity extends AppCompatActivity implements DataLoadedWithoutParamsListener {

    private SearchView searchView;
    private ViewPager viewPager;
    private PagerAdapterSearch pagerAdapterSearch;
    private Toolbar mToolbar;
    private TabLayout mTabHost;
    private ArrayList<Related> views;
    private ArrayList<Related> listens;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assearch);
        mProgressBar = (ProgressBar) findViewById(R.id.search_loading);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mTabHost = (TabLayout) findViewById(R.id.tabs);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        viewPager = (ViewPager) findViewById(R.id.viewpager_detailview);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_as_search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String query) {
        String url = URL.SEARCH + query.toString();
        new TaskLoadDataWithoutParams(this, url).execute();
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        mProgressBar.setVisibility(View.GONE);
        String jsonString = jsonObject.toString();
        Gson gson = new Gson();
        Search search = gson.fromJson(jsonString, Search.class);
        views = search.getView();
        listens = search.getListen();
        pagerAdapterSearch = new PagerAdapterSearch(views, listens, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapterSearch);
        mTabHost.setupWithViewPager(viewPager);
        pagerAdapterSearch.notifyDataSetChanged();

    }
}
