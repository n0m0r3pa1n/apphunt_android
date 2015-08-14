package com.apphunt.app.ui.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.apphunt.app.R;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.HashMap;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/9/15.
 */
public class CustomTwitterLoginButton extends TwitterLoginButton {
    private HashMap<String, Typeface> typefaces;
    public static final int NULL_RESOURCE = 0;

    public CustomTwitterLoginButton(Context context) {
        super(context);
        init(null);
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (typefaces == null) {
            typefaces = new HashMap<String, Typeface>();
        }

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AHTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.AHTextView_typefacePath);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (typefaces.containsKey(typefaceAssetPath)) {
                    typeface = typefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = getContext().getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    typefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }

        Resources res = this.getResources();

        setBackgroundResource(R.drawable.shape_provider_background);
        setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.ic_twitter), null);
        setPadding(60, 2, 8, 2);
        setText("Twitter");
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }
}
