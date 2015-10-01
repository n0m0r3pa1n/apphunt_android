package com.apphunt.app.ui.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.auth.LoginProviderFactory;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/28/15.
 * *
 * * NaughtySpirit 2015
 */
public class FollowButton extends AHButton {

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

        if (user.getId() != null && user.getId().equals(LoginProviderFactory.get(activity).getUser().getId())) {
            setVisibility(GONE);
        }
        setGravity(Gravity.CENTER);


    }
}
