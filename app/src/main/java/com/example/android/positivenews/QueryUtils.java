package com.example.android.positivenews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niina on 6.7.2017.
 */

//This class is to keep all the query related methods in a one, same place, vs. putting all this to Main


public final class QueryUtils {

    public static String LOG_TAG = QueryUtils.class.getName();

    //This is here just to make sure no-one else builds QueryUtils object:
    private QueryUtils() {

    }

    private static String titleValue;
    private static String sectionValue;
    private static String dateValue;
    private static String bitmapValue;

    //This is going to return a list of retrieved objects:
    private static List<ArticleDetails> extractFeaturesFromJson(String articleJson) {

        //First checking that if the JSON String is empty; in that case exit here:
        if (TextUtils.isEmpty(articleJson)) {
            return null;
        }

        //Here we know that JSON String has value; therefore we create an empty list where to add news:
        List<ArticleDetails> jsonNewsList = new ArrayList<>();

        //Try/Catch of JSON response:
        //First 'trying' to parse the needed elements, but if error occurs, it's catched + put to log

        try {

            JSONObject jsonArticleObject = new JSONObject(articleJson);

            JSONObject responseObject = jsonArticleObject.getJSONObject("response");

            //Checking that there is 'results' branch existing:
            if (responseObject.has("results")) {
                JSONArray resultsArray = responseObject.getJSONArray("results");


                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject currentArticleObject = resultsArray.getJSONObject(i);

                    if (currentArticleObject.has("webTitle")) {
                        titleValue = currentArticleObject.getString("webTitle");
                    } else {
                        titleValue = "No title available";
                    }

                    if (currentArticleObject.has("sectionName")) {
                        sectionValue = currentArticleObject.getString("sectionName");
                    } else {
                        sectionValue = "No section name available";
                    }

                    if (currentArticleObject.has("webPublicationDate")) {
                        dateValue = currentArticleObject.getString("webPublicationDate");
                    } else {
                        dateValue = "No publication date available";
                    }

                    String urlValue = currentArticleObject.getString("webUrl");

                    if (currentArticleObject.has("fields")) {
                        JSONObject fields = currentArticleObject.getJSONObject("fields");
                        if (fields.has("thumbnail")) {
                            bitmapValue = fields.getString("thumbnail");
                        }

                    } else {
                        bitmapValue = null;
                    }


                    ArticleDetails article = new ArticleDetails(titleValue, sectionValue, dateValue, urlValue, bitmapValue);

                    jsonNewsList.add(article);
                }

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem when parsing", e);
        }

        return jsonNewsList;
    }

    //Next: Making a new URL object called 'url' (it's value is the one in 'stringUrl'):

    private static URL createUrl(String stringUrl) {

        //First its value is null:
        URL url = null;

        //Then try/catch to create a new url:
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error when creating the new url", e);
        }

        return url;
    }

    //This method makes the HTTP request to the given url.
    //The return value is String

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        //Checking if url is empty; in that case exit (and return empty string ("")):
        if (url == null) {
            return jsonResponse;
        }
        //Because url has value, continuing to making the connection, read from stream etc.

        //Now to the actual connection request:
        //Used variables are null in the beginning:
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //Now trying to make the connection (With 'try' - 'catch' - 'finally'):

        try {

            //'urlConnection's new value = given url value + command to open connection using it:
            urlConnection = (HttpURLConnection) url.openConnection();
            //Commanding the following 'values' to the 'urlConnection':
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            //If connection is OK (code 200), start reading the input stream(= stream of bytes):
            if (urlConnection.getResponseCode() == 200) {

                //'inputStream' = given url + connection + its input bytes:
                inputStream = urlConnection.getInputStream();

                //Turning bytes into String ('readFromStream' method created by us, see a bit below)
                jsonResponse = readFromStream(inputStream);
            } else {
                //This happens, if the response code was something else than 200:
                Log.e(LOG_TAG, "Error in reading the stream, response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with retrieving the book results " + e);
        } finally {
            //Finally closing the connections (since we're done)
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        //'jsonResponse' = the retrieved bytes turned into string:
        return jsonResponse;
    }

    // Here the bytes are turned into string, as mentioned above(+ with help from BufferedStreamReader):
    private static String readFromStream(InputStream inputStream) throws IOException {

        //First creating stringBuilder = String that can be edited:
        StringBuilder stringBuilderOutput = new StringBuilder();

        //If inputStream has value:
        if (inputStream != null) {

            //We create the reader for the stream:
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //BufferedReader to read the chars faster:
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //line = line read with bufferedReader:
            String line = bufferedReader.readLine();
            //As long as the 'line' has something:
            while (line != null) {
                //The line is added to the 'editable string' + read again:
                stringBuilderOutput.append(line);
                line = bufferedReader.readLine();
            }
            //...And when the 'line' is null, the output is put to String form

        }
        return stringBuilderOutput.toString();

    }

    //'Final' method: to combine everything:

    public static final List<ArticleDetails> fetchArticleData(String requestUrl) {

        //Url object created:
        URL url = createUrl(requestUrl);

        //Make the HTTP request to 'url' + get JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with making url request", e);
        }

        //Extract the relevant fields + make a list from them:
        List<ArticleDetails> fetchedArticles = extractFeaturesFromJson(jsonResponse);
        return fetchedArticles;
    }


}
