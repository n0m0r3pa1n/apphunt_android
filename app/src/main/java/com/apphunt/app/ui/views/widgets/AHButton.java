package com.apphunt.app.ui.views.widgets;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.apphunt.app.R;
import com.apphunt.app.ui.interfaces.CustomButton;

import java.util.HashMap;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/14/15.
 * *
 * * NaughtySpirit 2015
 */
public class AHButton extends Button implements CustomButton {

    private HashMap<String, Typeface> typefaces;

    public AHButton(Context context) {
        super(context, null);
    }

    public AHButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public AHButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
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
                setTypefacePath(typefaceAssetPath);
            }
            array.recycle();
        }
    }

    public void setTypefacePath(String path) {
        Typeface typeface = null;

        if (typefaces.containsKey(path)) {
            typeface = typefaces.get(path);
        } else {
            AssetManager assets = getContext().getAssets();
            typeface = Typeface.createFromAsset(assets, path);
            typefaces.put(path, typeface);
        }

        setTypeface(typeface);
    }
}
