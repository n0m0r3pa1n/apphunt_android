package com.apphunt.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundsUtils {
    private static final String TAG = SoundsUtils.class.getName();

    private static SoundsUtils instance;
    private final Context ctx;
    private MediaPlayer mediaPlayer;

    public static SoundsUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new SoundsUtils(ctx);
        }

        return instance;
    }

    private SoundsUtils(Context ctx) {
        this.ctx = ctx;
    }

    public void playSound(int resSound) {
        try {
            mediaPlayer = MediaPlayer.create(ctx, resSound);
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't play the sound!\nException: " + e.getMessage());
        }

    }
}
