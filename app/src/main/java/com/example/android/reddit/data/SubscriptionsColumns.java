package com.example.android.reddit.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Chirag Desai on 22-07-2017.
 */

public interface SubscriptionsColumns{

    @DataType(DataType.Type.INTEGER)
    @AutoIncrement
    @PrimaryKey
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SR_NAME = "subreddit_name";

    @DataType(DataType.Type.TEXT)
    @DefaultValue("''")
    String POST_AFTER = "post_after";
}
