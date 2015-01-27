package com.shtaigaway.apphunt.utils;


public class Constants {
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_EMAIL = "profile_email";

    public static final String LOGIN_TYPE = "Facebook";
    public static final String PLATFORM = "Android";

    public static final String IS_DAILY_NOTIFICATION_SETUP_KEY = "isDisplayNotificationServiceSetup";

    // Fragment TAGs
    public static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    public static final String TAG_ADD_APP_FRAGMENT = "add_app_fragment";

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
