package com.example.android.reddit;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Chirag Desai on 21-07-2017.
 */

public class CustomApplication extends Application {
    public Tracker mTracker;

    public void startTracking(){
        if (mTracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.track_app);
            analytics.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker(){
        startTracking();
        return mTracker;
    }
}
