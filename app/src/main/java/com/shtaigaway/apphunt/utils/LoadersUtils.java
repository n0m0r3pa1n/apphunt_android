package com.shtaigaway.apphunt.utils;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.shtaigaway.apphunt.R;

import pl.droidsonroids.gif.GifImageView;

public class LoadersUtils {

    public static void showCenterLoader(Activity activity) {
        GifImageView centerLoader = (GifImageView) activity.findViewById(R.id.loader);

        if (centerLoader.getVisibility() != View.VISIBLE) {
            centerLoader.setBackgroundResource(R.drawable.loader);
            centerLoader.setVisibility(View.VISIBLE);
        }
    }

    public static void showCenterLoader(Activity activity, int resDrawable) {
        GifImageView centerLoader = (GifImageView) activity.findViewById(R.id.loader);

        if (centerLoader.getVisibility() != View.VISIBLE) {
            centerLoader.setVisibility(View.VISIBLE);
            centerLoader.setBackgroundResource(resDrawable);
        }
    }

    public static void hideCenterLoader(Activity activity) {
        GifImageView centerLoader = (GifImageView) activity.findViewById(R.id.loader);

        if (centerLoader.getVisibility() == View.VISIBLE) {
            centerLoader.setVisibility(View.GONE);
        }
    }

    public static void showBottomLoader(Activity activity) {
        RelativeLayout bottomLoaderLayout = (RelativeLayout) activity.findViewById(R.id.more_loader_layout);
        GifImageView bottomLoader = (GifImageView) activity.findViewById(R.id.more_loader);

        if (bottomLoaderLayout.getVisibility() != View.VISIBLE) {
            bottomLoader.setBackgroundResource(R.drawable.loader_white);
            SoundsUtils.getInstance(activity).playSound(R.raw.notification_1);
            bottomLoaderLayout.setVisibility(View.VISIBLE);
        }
    }

    public static void showBottomLoader(Activity activity, int resDrawable) {
        RelativeLayout bottomLoaderLayout = (RelativeLayout) activity.findViewById(R.id.more_loader_layout);
        GifImageView bottomLoader = (GifImageView) activity.findViewById(R.id.more_loader);

        if (bottomLoaderLayout.getVisibility() != View.VISIBLE) {
            SoundsUtils.getInstance(activity).playSound(R.raw.notification_1);
            bottomLoaderLayout.setVisibility(View.VISIBLE);
            bottomLoader.setBackgroundResource(resDrawable);
        }
    }

    public static void hideBottomLoader(Activity activity) {
        RelativeLayout bottomLoaderLayout = (RelativeLayout) activity.findViewById(R.id.more_loader_layout);

        if (bottomLoaderLayout.getVisibility() == View.VISIBLE) {
            bottomLoaderLayout.setVisibility(View.GONE);
        }
    }
}
