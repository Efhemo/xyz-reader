package com.example.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

import java.text.ParseException;
import java.util.Date;

class Adapter extends RecyclerView.Adapter<ArticleListActivity.ViewHolder> {
    private ArticleListActivity articleListActivity;
    private Cursor mCursor;

    public Adapter(ArticleListActivity articleListActivity, Cursor cursor) {
        this.articleListActivity = articleListActivity;
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ArticleListActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = articleListActivity.getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
        final ArticleListActivity.ViewHolder vh = new ArticleListActivity.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));

                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    Pair[] pairs = new Pair[3];
                    pairs[0] = new Pair<View, String>(vh.thumbnailView, "ImageTransition");
                    pairs[1] = new Pair<View, String>(vh.titleView, "titleTransition");
                    pairs[2] = new Pair<View, String>(vh.subtitleView, "otherTransition");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ArticleListActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                } else {

                    startActivity(intent);
                }*/
                articleListActivity.startActivity(intent);

                /*startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));*/
            }
        });
        return vh;
    }

    public Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return articleListActivity.dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(ArticleListActivity.TAG, ex.getMessage());
            Log.i(ArticleListActivity.TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ArticleListActivity.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(articleListActivity.START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    articleListActivity.outputFormat.format(publishedDate)
                    + "<br/>" + " by "
                    + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        }
        holder.thumbnailView.setImageUrl(
                mCursor.getString(ArticleLoader.Query.THUMB_URL),
                ImageLoaderHelper.getInstance(articleListActivity).getImageLoader());
        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
