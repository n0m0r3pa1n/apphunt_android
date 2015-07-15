package com.apphunt.app.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/9/15.
 */
public class CustomTwitterLoginButton extends TwitterLoginButton {

    public static final int NULL_RESOURCE = 0;

    public CustomTwitterLoginButton(Context context) {
        super(context);
        init();
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        setBackgroundResource(NULL_RESOURCE);
    }
}
