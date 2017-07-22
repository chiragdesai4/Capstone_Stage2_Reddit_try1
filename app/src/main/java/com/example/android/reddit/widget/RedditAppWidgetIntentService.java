package com.example.android.reddit.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.android.reddit.MainActivity;
import com.example.android.reddit.R;
import com.example.android.reddit.Utility;
import com.example.android.reddit.data.RedditProvider;
import com.example.android.reddit.data.SubscriptionsColumns;

/**
 * Created by Chirag Desai on 22-07-2017.
 */

public class RedditAppWidgetIntentService extends IntentService {

    public RedditAppWidgetIntentService() {
        super("RedditAppWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //This is merely a proof of concept method, it doesn't really add any value to the app
        if (intent != null) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(this, RedditAppWidget.class));

            Cursor data = getContentResolver().query(
                    RedditProvider.Subscriptions.randomSubscription,
                    null,
                    null,
                    null,
                    null);

            if (data == null || !data.moveToFirst()) {
                Utility.closeCursor(data);
                return;
            }
            String randomSubscription =
                    data.getString(data.getColumnIndex(SubscriptionsColumns.SR_NAME));

            for (int appWidgetId : appWidgetIds) {
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

                // Construct the RemoteViews object
                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.reddit_app_widget);
                views.setTextViewText(R.id.appwidget_text, randomSubscription);
                views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
