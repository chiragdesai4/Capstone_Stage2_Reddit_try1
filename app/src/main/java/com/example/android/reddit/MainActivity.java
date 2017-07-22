package com.example.android.reddit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.reddit.client.RedditRestClient;
import com.example.android.reddit.data.FetchPostTask;
import com.example.android.reddit.data.Post;
import com.example.android.reddit.data.RedditProvider;
import com.example.android.reddit.data.SubscriptionsColumns;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements FetchPostTask.TaskDelegate {

    private static final String TOKEN_URL = "vl/access_token";
    private static final String DEVICE_ID = UUID.randomUUID().toString();

    @Bind(R.id.card_view_post)
    CardView mCardView;

    @Bind(R.id.adView)
    AdView mAdVIew;

    @Bind(R.id.button_subscribe)
    AppCompatButton mSubscribeButton;

    @Bind(R.id.button_next_post)
    AppCompatButton mNextButton;

    @Bind(R.id.post_title)
    TextView mTitleTextView;

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;

    private Post mPost;

    @OnClick(R.id.card_view_post)
    public void openPontInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Utility.buildCommentsUri(mPost.getPermalink()));
        startActivity(intent);
    }

    @OnClick(R.id.button_next_post)
    public void fetchNextPost() {
        updateAafterField();
        fetchPost();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        loadAdBanner();
        getAuthToken();
        fetchPost();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tracker tracker = ((CustomApplication) getApplication()).getTracker();
        tracker.setScreenName("Main Activity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_subscribe:
                startActivity(new Intent(this, SubscribeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadAdBanner(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("F178C54519B81461A49075E590A1E90C")
                .build();
        mAdVIew.loadAd(adRequest);
    }

    public void launchSubscribeSetting(View unusedView){
        startActivity(new Intent(this, SubscribeActivity.class));
    }

    private void getAuthToken(){
        try{
            new RedditRestClient().getToken(TOKEN_URL, DEVICE_ID);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void fetchPost(){
        Cursor cursor = getContentResolver().query(RedditProvider.Subscriptions.randomSubscription,
                null,
                null,
                null,
                null);
        if (cursor != null && cursor.moveToFirst()){
            String subReddit = cursor.getString(
                    cursor.getColumnIndex(SubscriptionsColumns.SR_NAME));

            String postAfter = cursor.getString(
                    cursor.getColumnIndex(SubscriptionsColumns.POST_AFTER));

            new FetchPostTask(this).execute(subReddit, postAfter);
        }else {
            mCardView.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
            mSubscribeButton.setVisibility(View.VISIBLE);
        }
        Utility.closeCursor(cursor);
    }

    private void updateAafterField(){
        ContentValues values = new ContentValues();
        values.put(SubscriptionsColumns.POST_AFTER, mPost.getAfter());

        getApplicationContext().getContentResolver().update(
                RedditProvider.Subscriptions.withSubredditName(mPost.getSubreddit()),
                values,
                null,
                null);
    }

    @Override
    public void taskCompletionResult(Post result) {
        mPost = result;
        mTitleTextView.setText(result.getTitle());
    }
}
