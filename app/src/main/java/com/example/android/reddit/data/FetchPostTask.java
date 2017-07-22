package com.example.android.reddit.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chirag Desai on 21-07-2017.
 */

public class FetchPostTask extends AsyncTask<String, Void, Post> {
    private static final String LOG_TAG = FetchPostTask.class.getSimpleName();
    private TaskDelegate mDelegate;

    public FetchPostTask(TaskDelegate delegate) {
        mDelegate = delegate;
    }


    @Override
    protected Post doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String postsJsonString;
        String subReddit = params[0];


        try {
            Uri builder = Uri.parse("http://www.reddit.com/r").buildUpon()
                    .appendPath(subReddit)
                    .appendPath(".json")
                    .appendQueryParameter("limit", "l")
                    .appendQueryParameter("after", params[1]).build();

            URL url = new URL(builder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            postsJsonString = buffer.toString();

            try {
                return getPostDataFromJson(postsJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing Stream", e);
                }
            }
        }
        return null;
    }

    private Post getPostDataFromJson(String postsJsonString) throws JSONException {

        final String REDDIT_CHILDREN = "children";
        final String REDDIT_DATA = "data";
        final String REDDIT_TITTLE = "title";
        final String REDDIT_AFTER = "after";
        final String REDDIT_SUBREDDIT = "subreddit";
        final String REDDIT_PERMALINK = "permalink";

        JSONObject postsJson = new JSONObject(postsJsonString);
        JSONObject dataJson = postsJson.getJSONObject(REDDIT_DATA);
        JSONArray childrenArray = dataJson.getJSONArray(REDDIT_CHILDREN);
        JSONObject jsonPost = childrenArray.getJSONObject(0).getJSONObject(REDDIT_DATA);

        String title = jsonPost.getString(REDDIT_TITTLE);
        String after = dataJson.getString(REDDIT_AFTER);
        String subReddit = jsonPost.getString(REDDIT_SUBREDDIT);
        String permalink = jsonPost.getString(REDDIT_PERMALINK);

        Post post = new Post();
        post.setTitle(title);
        post.setAfter(after);
        post.setSubReddit(subReddit);
        post.setPermalink(permalink);

        return post;
    }

    @Override
    protected void onPostExecute(Post post) {
        mDelegate.taskCompletionResult(post);
    }

    public interface TaskDelegate {
        void taskCompletionResult(Post result);
    }
}
