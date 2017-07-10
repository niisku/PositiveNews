package com.example.android.positivenews;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

/**
 * Created by Niina on 6.7.2017.
 */

//Loader file to fetch the data only once (vs. without this, requests start from scratch
//always when e.g. phone is rotated)

public class ArticleLoader extends AsyncTaskLoader<List<ArticleDetails>> {

    //Log tag for long information:
    private static final String LOG_TAG = ArticleLoader.class.getName();

    //Query url:
    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        Log.i(LOG_TAG, "Here 'onStartLoading' is called..");
        forceLoad();
    }

    @Override
    public List<ArticleDetails> loadInBackground() {

        Log.i(LOG_TAG, "TEST: Here loadInBackground() called...");

        //First checked that the url has value; if not, then exit:
        if (mUrl == null) {
            return null;
        }
        //Here the url has got value; therefore
        // 1. Perform the network request, 2. parse the response, 3. extract a list of articles:

        List<ArticleDetails> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
}
