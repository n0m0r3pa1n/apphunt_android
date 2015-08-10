package com.apphunt.app.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.apphunt.app.R;
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

        Resources res = this.getResources();

        setBackgroundResource(R.drawable.shape_provider_background);
        setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_twitter), null);
        setPadding(60, 2, 8, 2);
        setText("Twitter");
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setTypeface(Typeface.DEFAULT);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }
}
