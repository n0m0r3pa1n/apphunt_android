package com.apphunt.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void init(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Saving a String value into the SharedPreferences Manager.
     *
     * @param key   preference's key.
     * @param value preference's value.
     */
    public static void setPreference(String key, String value) {
        editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Saving an Int value into the SharedPreferences Manager.
     *
     * @param key   preference's key.
     * @param value preference's value.
     */
    public static void setPreference(String key, int value) {
        editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Saving a Boolean value into the SharedPreferences Manager.
     *
     * @param key   preference's key.
     * @param value preference's value.
     */
    public static void setPreference(String key, boolean value) {
        editor = sharedPreferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Saving a Long value into the SharedPreferences Manager.
     *
     * @param key   preference's key.
     * @param value preference's value.
     */
    public static void setPreference(String key, long value) {
        editor = sharedPreferences.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Extracting a String value from the SharedPreferences Manager.
     *
     * @param key preference's key.
     */
    public static String getStringPreference(String key) {

        return sharedPreferences.getString(key, null);
    }

    /**
     * Extracting an Int value from the SharedPreferences Manager.
     *
     * @param ctx application's context.
     * @param key preference's key.
     */
    public static int getIntPreference(Context ctx, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Extracting an Int value from the SharedPreferences Manager.
     *
     * @param key          preference's key.
     * @param defaultValue default return value
     */
    public static int getIntPreference(String key, int defaultValue) {

        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Extracting a Boolean value from the SharedPreferences Manager.
     *
     * @param key preference's key.
     */
    public static Boolean getBooleanPreference(String key) {
        if (key.equals(Constants.SETTING_NOTIFICATIONS_ENABLED) || key.equals(Constants.IS_SOUNDS_ENABLED)) {
            return sharedPreferences.getBoolean(key, true);
        } else {
            return sharedPreferences.getBoolean(key, false);
        }
    }

    /**
     * Extracting an Long value from the SharedPreferences Manager.
     *
     * @param key preference's key.
     */
    public static long getLongPreference(String key) {

        return sharedPreferences.getLong(key, -1);
    }

    /**
     * Removing a value from the SharedPreferences Manager.
     *
     * @param key preference's key.
     */
    public static void removePreference(String key) {
        editor = sharedPreferences.edit();

        editor.remove(key);
        editor.apply();
    }
}