package com.apphunt.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.apphunt.app.constants.Constants;

public class SoundsUtils {
    private static final String TAG = SoundsUtils.class.getName();

    public static void playSound(Context ctx, int resSound) {
        if (SharedPreferencesHelper.getBooleanPreference(Constants.IS_SOUNDS_ENABLED)) {
            try {
                MediaPlayer mediaPlayer = MediaPlayer.create(ctx, resSound);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't play the sound!\nException: " + e.getMessage());
            }
        }
    }

    public static void vibrate(Context ctx) {
        if (SharedPreferencesHelper.getBooleanPreference(Constants.IS_SOUNDS_ENABLED)) {
            Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
    }
}
