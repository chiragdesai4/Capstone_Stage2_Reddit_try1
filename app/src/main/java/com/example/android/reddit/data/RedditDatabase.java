package com.example.android.reddit.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Chirag Desai on 22-07-2017.
 */

@Database(version = RedditDatabase.VERSION,
packageName = "com.example.android.reddit.provider")

public final class RedditDatabase {
    public static final int VERSION = 1;

    @Table(SubscriptionsColumns.class)
    public static final String SUBSCRIPTIONS = "subscriptions";
}
