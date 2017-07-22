package com.example.android.reddit.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Chirag Desai on 22-07-2017.
 */

@ContentProvider(authority = RedditProvider.AUTHORITY,
        packageName = "com.example.android.reddit.provider",
        database = RedditDatabase.class)

public final class RedditProvider {
    public static final String AUTHORITY =
            "com.example.android.reddit.data.RedditProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String SUBSCRIPTIONS = "subscriptions";
    }

    @TableEndpoint(table = RedditDatabase.SUBSCRIPTIONS)
    public static class Subscriptions {

        @ContentUri(
                path = Path.SUBSCRIPTIONS,
                type = "vnd.android.cursor.item/subscription")
        public static final Uri CONTENT_URI = buildUri(Path.SUBSCRIPTIONS);

        @ContentUri(
                path = Path.SUBSCRIPTIONS + "/random",
                type = "vnd.android.cursor.item/subscription",
                defaultSort = "RANDOM()",
                limit = "1")
        public static final Uri randomSubscription = buildUri(Path.SUBSCRIPTIONS, "random");

        @InexactContentUri(
                name = "SUBSCRIPTIONS_SR_NAME",
                path = Path.SUBSCRIPTIONS + "/*",
                type = "vnd.android.cursor.item/subscription",
                whereColumn = SubscriptionsColumns.SR_NAME,
                pathSegment = 1)
        public static Uri withSubredditName(String name) {
            return buildUri(Path.SUBSCRIPTIONS, name);
        }
    }
}
