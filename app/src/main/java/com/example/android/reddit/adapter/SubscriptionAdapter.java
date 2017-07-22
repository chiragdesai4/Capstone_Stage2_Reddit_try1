package com.example.android.reddit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.reddit.R;
import com.example.android.reddit.data.RedditProvider;
import com.example.android.reddit.data.SubscriptionsColumns;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Chirag Desai on 22-07-2017.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private final static String CURSOR_MOVE_ERROR = "Could not move cursor to position";

    private Cursor mCursor;
    private Context mContext;

    public SubscriptionAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subscription, parent, false);

        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)){
            throw new IllegalStateException(CURSOR_MOVE_ERROR + position);
        }

        String title = mCursor.getString(mCursor.getColumnIndex(SubscriptionsColumns.SR_NAME));
        holder.title.setText(title);
    }

    public void swapCursor(Cursor newCursor){
        this.mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.text_view_subscription_name)
        TextView title;

        @Bind(R.id.button_delete_subscription)
        ImageButton deleteButton;

        public SubscriptionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.button_delete_subscription)
        public void deleteSubscription() {
            mCursor.moveToPosition(getAdapterPosition());
            String name = mCursor.getString(mCursor.getColumnIndex(SubscriptionsColumns.SR_NAME));
            mContext.getContentResolver().delete(
                    RedditProvider.Subscriptions.withSubredditName(name),
                    null,
                    null);
        }
    }
}
