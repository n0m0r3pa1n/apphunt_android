package com.apphunt.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.WebviewActivity;
import com.apphunt.app.api.apphunt.models.posts.BlogPost;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-12-17.
 */
public class BlogPostRow extends LinearLayout {
    public static final String TAG = BlogPostRow.class.getSimpleName();

    private Context context;
    private int postId;
    private String pictureUrl;

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.picture)
    ImageView picture;

    @InjectView(R.id.excerpt)
    TextView excerpt;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.post_container)
    RelativeLayout postContainer;

    public BlogPostRow(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public BlogPostRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public BlogPostRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BlogPostRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_blog_post_row, this, true);
        ButterKnife.inject(this, view);

        this.context = context;
    }

    public void setBlogPost(final BlogPost blogPost) {
        title.setText(Html.fromHtml(blogPost.getTitle()));
        Log.d(TAG, "setBlogPost: " + blogPost.getExcerpt());
        excerpt.setText(Html.fromHtml(blogPost.getExcerpt()).toString());
        date.setText(blogPost.getDate());
        this.postId = blogPost.getId();
        if(!TextUtils.isEmpty(blogPost.getFeaturedImageUrl())) {
            Picasso.with(context).load(blogPost.getFeaturedImageUrl()).into(picture);
        } else {
            Picasso.with(context).load(R.drawable.ic_launcher).into(picture);
        }
        postContainer.setOnClickListener(null);
        postContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = blogPost.getLink();
                FlurryWrapper.logEvent(TrackingEvents.UserViewedNews, new HashMap<String, String>() {{
                    put("url", url);
                }});
                Intent intent = new Intent(getContext(), WebviewActivity.class);
                intent.putExtra(Constants.EXTRA_URL, url);
                getContext().startActivity(intent);
            }
        });
    }
}
