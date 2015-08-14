package com.apphunt.app.ui.views.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.apphunt.app.R;

import java.util.HashMap;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/14/15.
 * *
 * * NaughtySpirit 2015
 */
public class AHTextView extends TextView {
    private HashMap<String, Typeface> typefaces;

    public AHTextView(Context context) {
        super(context, null);
    }

    public AHTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public AHTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context ctx, AttributeSet attrs) {

        if (typefaces == null) {
            typefaces = new HashMap<String, Typeface>();
        }

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = ctx.obtainStyledAttributes(attrs, R.styleable.AHTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.AHTextView_typefacePath);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (typefaces.containsKey(typefaceAssetPath)) {
                    typeface = typefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = ctx.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    typefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }
    }
}
