package com.apphunt.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundsUtils {
    private static final String TAG = SoundsUtils.class.getName();

    public static void playSound(Context ctx, int resSound) {
        if (SharedPreferencesHelper.getBooleanPreference(ctx, Constants.IS_SOUNDS_ENABLED)) {
            try {
                MediaPlayer mediaPlayer = MediaPlayer.create(ctx, resSound);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't play the sound!\nException: " + e.getMessage());
            }
        }
    }
}
