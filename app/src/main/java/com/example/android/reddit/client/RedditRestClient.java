package com.example.android.reddit.client;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.example.android.reddit.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Chirag Desai on 21-07-2017.
 */

public class RedditRestClient {

    private static final String LOG_TAG = RedditRestClient.class.getSimpleName();
    private static final String BASE_URL = "https://www.reddit.com/api/";
    private static final String SEARCH_NAMES_URL = "search_reddit_names.json";

    public static String NAME_KEY = "names";
    private static String CLIENT_ID = "SnY53e27lhsEow";
    private static String CLIENT_SECRET = "";
    private static String REDIRECT_URI = "redditreader://com.example.android.reddit";

    private static String GRANT_TYPE_KEY = "grant_type";
    private static String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static String REDIRECT_URI_KEY = "redirect_uri";
    private static String DEVICE_ID_KEY = "device_id";
    private static String EXACT_KEY = "exact";
    private static String OVER_18_KEY = "include_over_18";
    private static String QUERY_KEY = "query";
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public RedditRestClient() {
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        httpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void getToken(String relativeUrl, String deviceId)
            throws JSONException {
        httpClient.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        RequestParams requestParams = new RequestParams();
        requestParams.put(GRANT_TYPE_KEY, GRANT_TYPE);
        requestParams.put(REDIRECT_URI_KEY, REDIRECT_URI);
        requestParams.put(DEVICE_ID_KEY, deviceId);

        post(relativeUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(LOG_TAG, "response: " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.i(LOG_TAG, "status code: " + statusCode);
            }
        });
    }

    public void getSubreddit(String query, final ResponseListener listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.put(EXACT_KEY, true);
        requestParams.put(OVER_18_KEY, false);
        requestParams.put(QUERY_KEY, query);

        post(SEARCH_NAMES_URL, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(LOG_TAG, "response: " + response.toString());
                try {
                    String name = Utility.formatSubredditName(response.getString(NAME_KEY));
                    Log.i(LOG_TAG, "name: " + name);
                    listener.OnGetSubredditResponse(true, name);
                } catch (JSONException j) {
                    j.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.i(LOG_TAG, "status code: " + statusCode);
                listener.OnGetSubredditResponse(false, Integer.toString(statusCode));
            }
        });
    }

    public interface ResponseListener {
        void OnGetSubredditResponse(boolean isReddit, String content);
    }

}

