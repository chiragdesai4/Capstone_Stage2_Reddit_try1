package com.example.android.reddit;

import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Chirag Desai on 21-07-2017.
 */

public class Utility {

    public static String formatSubredditName(String subredditName){
        return subredditName.substring(2, subredditName.length() - 2);
    }

    public static void closeCursor(Cursor cursor){
        if (null != cursor){
            cursor.close();
        }
    }

    public static Uri buildCommentsUri(String path){
        return Uri.parse("http://i.reddit.com".concat(path));
    }
}
