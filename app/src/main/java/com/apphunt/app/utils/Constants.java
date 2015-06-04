package com.apphunt.app.utils;


import java.util.HashMap;

public class Constants {
    public static final String PACKAGE_NAME = "com.apphunt.app";

    // User's Keys
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_EMAIL = "profile_email";
    public static final String KEY_USER_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_USER_COVER_PICTURE = "profile_cover_picture";
    public static final String KEY_USER_TEXT_COLOR = "text_color";

    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public static final String KEY_DATA = "data";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_SHOW_SETTINGS = "show_settings";
    public static final String KEY_SHOW_RATING = "show_rating";
    public static final String KEY_LOGIN_PROVIDER = "login_provider";
    public static final String KEY_NOTIFICATION_TYPE = "started_from_notification";
//    public static final String KEY_DAILY_REMINDER_NOTIFICATION = "daily_reminder_notification";
//    public static final String KEY_DAILY_REMINDER_NOTIFICATION = "app_approved";
    public static final String KEY_APP_ID = "app_id";
    public static final String KEY_APP_NAME = "app_name";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String KEY_NOTIFICATION_TITLE = "notification_title";
    public static final String KEY_NOTIFICATION_MSG = "notification_msg";
    public static final String KEY_SEARCH_QUERY = "search_query";

    public static final String PLATFORM = "Android";

    // Login
    public static final String APPHUNT_ADMIN_USER_ID = "54be5d68e4b0d3cacca686c5";
    public static final int USER_SKIP_LOGIN_PERCENTAGE = 50;

    // Settings
    public static final String SETTING_NOTIFICATIONS_ENABLED = "isDisplayNotificationServiceEnabled";
    public static final String IS_SOUNDS_ENABLED = "isSoundsEnabled";
    public static final String WAS_SPLASH_SHOWN = "wasSplashShown";

    // Fragment TAGs
    public static final String TAG_LOGIN_FRAGMENT = "login_fragment";
    public static final String TAG_SELECT_APP_FRAGMENT = "add_app_fragment";
    public static final String TAG_SAVE_APP_FRAGMENT = "add_save_fragment";
    public static final String TAG_SETTINGS_FRAGMENT = "settings_fragment";
    public static final String TAG_NOTIFICATION_FRAGMENT = "notification_fragment";
    public static final String TAG_SUGGEST_FRAGMENT = "suggest_fragment";
    public static final String TAG_APP_DETAILS_FRAGMENT = "app_details_fragment";
    public static final String TAG_APPS_LIST_FRAGMENT = "apps_list_fragment";

    // RequestCodes
    public static final int REQUEST_NETWORK_SETTINGS = 3;

    // Actions
    public static final String ACTION_ENABLE_NOTIFICATIONS = "com.apphunt.app.action.ENABLE_NOTIFICATIONS";

    // Smart Rate
    public static final String SMART_RATE_LOCATION_APP_SAVED = "SaveApp#appSaved";
    public static final String SMART_RATE_LOCATION_APP_VOTED = "TrendingApps#appVoted";
    public static final String APP_SPICE_APP_ID = "54d4a61f6275f00300b032d9";

    // Sharing
    public static final String GOOGLE_PLAY_APP_URL = "https://play.google.com/store/apps/details?id=com.apphunt.app";
    public static final String BIT_LY_GOOGLE_PLAY_URL = "http://bit.ly/1umy2AV";
    public static final String LAUNCHROCK_ICON = "https://launchrock-assets.s3.amazonaws.com/logo-files/LWPRHM35_1421410706452.png?_=4";

    // Invites
    public static final int REQUEST_ACCOUNT_EMAIL = 5;

    public static final String DEFAULT_NOTIFICATION_TITLE = "Check out today's useful apps";
    public static final String DEFAULT_NOTIFICATION_MSG = "Which are the trending apps today?";
    public static final String FLURRY_API_KEY = "TF65K4T659FTCPXGMNG3";
    public static final String FLURRY_DEBUG_API_KEY = "TF65K4T659FTCPXGMNG4";
    public static final int SEARCH_RESULT_COUNT = 30;

    // GCM
    public static final String GCM_SENDER_ID = "437946264894";

    public static final HashMap<String, Integer> NOTIFICATION_TYPE_TO_ID = new HashMap<String, Integer>() {{
        put("dailyReminder", 1);
        put("appApproved", 2);
        put("appRejected", 3);
        put("userComment", 4);
        put("userMentioned", 5);
    }};

    public static final HashMap<String, Integer> NOTIFICATION_TYPE_TO_REQUEST_CODE = new HashMap<String, Integer>() {{
        put("dailyReminder", 101);
        put("appApproved", 102);
        put("appRejected", 103);
        put("userComment", 104);
        put("userMentioned", 105);
    }};
    public static final String TWITTER_CONSUMER_KEY = "2GwWIq8PXArLO1YKieGNsAKQa";
    public static final String TWITTER_CONSUMER_SECRET = "GG81rZvwLnFdxzSdtASsQMDaWZVr7bzzqRKBCWgnWCmpQqx5VK";

    public static final int TRENDING_APPS = 1;
    public static final int TOP_HUNTERS = 2;
    public static final int TOP_APPS = 3;
    public static final int SETTINGS = 5;
    public static final int ABOUT = 6;

    public enum ItemType {
        SEPARATOR(0), ITEM(1), MORE_APPS(2),
        COMMENT(3), SUBCOMMENT(4);

        private int value;

        ItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
