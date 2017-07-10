package com.example.android.positivenews;

/**
 * Created by Niina on 6.7.2017.
 */

public class ArticleDetails {

    private String mTitle;
    private String mSection;
    private String mDate;
    private String mUrl;
    private String mBitmap;

    public ArticleDetails(String title, String section, String date, String url, String bitmap) {

        mTitle = title;
        mSection = section;
        mDate = date;
        mUrl = url;
        mBitmap = bitmap;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmBitmap() {
        return mBitmap;
    }
}
