package com.shtaigaway.apphunt.utils;


public class Constants {
    public static final String PACKAGE_NAME = "com.shtaigaway.apphunt";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_EMAIL = "profile_email";
    public static final String KEY_DATA = "data";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_SHOW_SETTINGS = "show_settings";

    public static final String LOGIN_TYPE = "Facebook";
    public static final String PLATFORM = "Android";

    public static final String IS_DAILY_NOTIFICATION_ENABLED = "isDisplayNotificationServiceEnabled";

    // Fragment TAGs
    public static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    public static final String TAG_SELECT_APP_FRAGMENT = "add_app_fragment";
    public static final String TAG_SAVE_APP_FRAGMENT = "add_save_fragment";
    public static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    public static final String TAG_NOTIFICATION_FRAGMENT = "notification_fragment";

    // RequestCodes
    public static final int REQUEST_NETWORK_SETTINGS = 3;

    // Actions
    public static final String ACTION_ENABLE_NOTIFICATIONS = "com.shtaigaway.apphunt.action.ENABLE_NOTIFICATIONS";

    public enum ItemType {
        SEPARATOR(0), ITEM(1), MORE_APPS(2);

        private int value;

        ItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
