package com.apphunt.app.ui.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.users.UserFollowApiEvent;
import com.apphunt.app.event_bus.events.api.users.UserUnfollowApiEvent;
import com.squareup.otto.Subscribe;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/28/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowButton extends AHButton {

    private static final String TAG = FollowButton.class.getSimpleName();

    private Activity activity;
    private User user;

    public FollowButton(Context context) {
        super(context, null);
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void init(Context ctx, AttributeSet attrs) {
        super.init(ctx, attrs);
    }

    public void init(Activity activity, User user) {
        this.user = user;
        this.activity = activity;

        if (user.getId().equals(LoginProviderFactory.get(activity).getUser().getId())) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);

            if (user.isFollowing()) {
                unfollow();
            } else {
                follow();
            }
        }
        setGravity(Gravity.CENTER);
        setOnClickListener(customOnClickListener);
    }

    private OnClickListener customOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String followingId = LoginProviderFactory.get(activity).getUser().getId();
            if (user.isFollowing()) {
                ApiClient.getClient(activity).unfollowUser(followingId, user.getId());
            } else {
                ApiClient.getClient(activity).followUser(followingId, user.getId());
            }
        }
    };

    private void follow() {
        this.setBackgroundResource(R.drawable.btn_follow);
        this.setText(R.string.follow);
        this.setTextColor(Color.WHITE);
    }

    private void unfollow() {
        this.setBackgroundResource(R.drawable.btn_unfollow);
        this.setText(R.string.unfollow);
        this.setTextColor(getResources().getColor(R.color.bg_primary));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (user == null) {
            return;
        }

        if (user.isFollowing()) {
            unfollow();
        } else {
            follow();
        }
    }

    @Subscribe
    public void onFollowResponse(UserFollowApiEvent event) {
        if(!event.getUserId().equals(user.getId())) {
            return;
        }
        Log.e(TAG, "onFollowResponse: " + event.isSuccess());
        if (event.isSuccess()) {
            unfollow();
            user.setIsFollowing(true);
        } else {
            Log.e(TAG, "Follow error");
        }
    }

    @Subscribe
    public void onUnfollowResponse(UserUnfollowApiEvent event) {
        if(!event.getUserId().equals(user.getId())) {
            return;
        }
        Log.e(TAG, "onUnfollowResponse: " + event.isSuccess());
        if (event.isSuccess()) {
            follow();
            user.setIsFollowing(false);
        } else {
            Log.e(TAG, "Unfollow error");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }
}
