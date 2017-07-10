package com.example.android.positivenews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, LoaderCallbacks<List<ArticleDetails>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final String FINAL_URL = "http://content.guardianapis.com/search?q=happiness&api-key=test&show-fields=thumbnail&order-by=newest";

    private static final int ARTICLE_LOADER_ID = 1;

    private ArticleAdapter mAdapter;

    private TextView mEmptyTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "TEST: Here MainActivity's onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the list place from xml + name it 'articleListView'
        ListView articleListView = (ListView) findViewById(R.id.list);

        //Setting the empty view xml to the list view:
        mEmptyTextView = (TextView) findViewById(R.id.empty_textview);
        articleListView.setEmptyView(mEmptyTextView);

        //Create an adapter that takes an empty list of articles as an input:
        mAdapter = new ArticleAdapter(this, new ArrayList<ArticleDetails>());
        //Attach the adapter to listview so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        //Next: making ItemClickListener to the listview, which sends an intent to open web server
        //to the current article's url:
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //Here deciding what happens when an list item is being clicked:
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Finding + naming the current article:
                ArticleDetails currentArticle = mAdapter.getItem(position);

                //Convert the String url to URI object (= can then be passed to the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getmUrl());

                //Create a new intent to view the article URI:
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });


        //Fetching first the isConnected method (that checks the internet connection) and then creating the loader:
        if (isConnected()) {
            //Create LoaderManager (to interact with loaders):
            LoaderManager loaderManager = getLoaderManager();
            //Initialize the loader; Pass ID, null for Bundle, and this activity:
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            //Otherwise display error, but first hide the loading indicator
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.string_no_internet);
        }


    }

    //Here: LoaderCallbacks() interface + its 3 methods;
    // 1. onCreateLoader() = Create & return a new loader for the given ID
    // (= fetches the news data from web server)
    // 2. onLoadFinished() = Called when previously created loader has finished its loading
    // (= loader has finished downloading data on the background thread, so here updating
    // the UI with the list of articles)
    // 3. onLoadReset() = Called when prev. created loader is being reset, making its data unavailable
    // (= E.g. If decided to download data from new URL (bc of new search term), current list
    // should be cleared)

    @Override
    public Loader<List<ArticleDetails>> onCreateLoader(int id, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: Here MainActivity's onCreateLoader() called");

        //Checking the internet connection:
        if (isConnected()) {
            return new ArticleLoader(this, FINAL_URL);
        } else {
            //Otherwise display error, but first hide the loading indicator
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.string_no_internet);
            return null;
        }
    }


    //Here: Updating the dataset in the adapter to update the UI.
    @Override
    public void onLoadFinished(Loader<List<ArticleDetails>> loader, List<ArticleDetails> data) {

        //Hiding the loading spinner:
        ProgressBar hideSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        hideSpinner.setVisibility(View.GONE);
        //Informing that there's no articles.
        mEmptyTextView.setText(R.string.string_no_articles);

        //Clearing the adapter from previous data:
        mAdapter.clear();

        //Removing the refresh icon animation:
        mSwipeRefreshLayout.setRefreshing(false);

        //If there's a list of articles, added to the adapter's data set -> listView updates:
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

    }

    //And here: Clearing the adapter when its resetted.
    @Override
    public void onLoaderReset(Loader<List<ArticleDetails>> loader) {
        Log.i(LOG_TAG, "TEST: Here onLoaderReset() called");

        mAdapter.clear();
    }

    //This is for the swipe & refresh; when swiped, the loader (& therefore articles) are refreshed:
    @Override
    public void onRefresh() {

        //First checking the internet connection:
        if (isConnected()) {
            getLoaderManager().restartLoader(ARTICLE_LOADER_ID, null, this);

        } else {
            //Hiding the loading spinner:
            ProgressBar hideSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
            hideSpinner.setVisibility(View.GONE);
            //Informing that there's no articles.
            mEmptyTextView.setText(R.string.string_no_internet);

            //Clearing the adapter from previous data:
            mAdapter.clear();

            //Removing the refresh icon animation:
            mSwipeRefreshLayout.setRefreshing(false);

        }

    }

    //Helper method for internet connection checks:
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //2: Get information about current network:
        NetworkInfo networkInfo = (NetworkInfo) connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}