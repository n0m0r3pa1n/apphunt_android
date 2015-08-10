package com.apphunt.app.ui.views.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import com.apphunt.app.R;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class CustomGooglePlusButton extends Button {
    public CustomGooglePlusButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public CustomGooglePlusButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGooglePlusButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources res = this.getResources();

        setBackgroundResource(R.drawable.shape_provider_background);
        setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_google), null);
        setPadding(60, 2, 8, 2);
        setText("Google+");
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }
}
