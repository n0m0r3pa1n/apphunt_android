package com.apphunt.app.constants;


import com.google.gson.annotations.SerializedName;

public class Constants {
    public static final String PACKAGE_NAME = "com.apphunt.app";

    //TODO: use production url before release
//    public static final String MAIN_URL = "apphunt-dev.herokuapp.com";
    public static final String MAIN_URL = "apphunt-dev.herokuapp.com";
//    public static final String MAIN_URL = "7eebac65.ngrok.io";
    public static final String BASE_SOCKET_URL = "http://" + MAIN_URL + "";
    public static final String BASE_URL = "http://" + MAIN_URL + "";

    // User's Keys
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_EMAIL = "profile_email";
    public static final String KEY_USER_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_USER_COVER_PICTURE = "profile_cover_picture";
    public static final String KEY_USER_TEXT_COLOR = "text_color";

    public static final String KEY_USER_PROFILE = "user_profile";
    public static final String KEY_NOTIFICATION_ID = "notification_id";
    public static final String KEY_NOTIFICATION_APP_ID = "appId";

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
    public static final String KEY_TITLE = "fragment_title";
    public static final String KEY_COLLECTION = "collection";
    public static final String KEY_SHOW_SHADOW = "show_shadow";
    public static final String KEY_SHOW_CONTINUE = "show_continue";
    public static final String KEY_LAST_SEEN_EVENT_ID = "last_seen_event_id";
    public static final String KEY_LAST_SEEN_EVENT_DATE = "last_seen_event_date";

    public static final String PLATFORM = "Android";

    // Login
    public static final String APPHUNT_ADMIN_USER_ID = "54be5d68e4b0d3cacca686c5";
    public static final int USER_SKIP_LOGIN_PERCENTAGE = 50;

    // Settings
    public static final String SETTING_NOTIFICATIONS_ENABLED = "isDisplayNotificationServiceEnabled";
    public static final String IS_SOUNDS_ENABLED = "isSoundsEnabled";
    public static final String IS_INSTALL_NOTIFICATION_ENABLED = "isInstallNotificationEnabled";
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
    public static final String TAG_TOP_APPS_FRAGMENT = "top_apps_fragment";
    public static final String TAG_TOP_HUNTERS_FRAGMENT = "top_hunters_fragment";
    public static final String TAG_SELECT_COLLECTION_FRAGMENT = "select_collection_fragment";
    public static final String TAG_CREATE_COLLECTION_FRAGMENT = "create_collection_fragment";
    public static final String TAG_COLLECTION_DETAILS_FRAGMENT = "collection_details_fragment";
    public static final String TAG_CHOOSE_COLLECTION_BANNER_FRAGMENT = "choose_collection_banner_fragment";
    public static final String TAG_COMMENTS = "comments_fragment";
    public static final String TAG_SEARCH_RESULTS_FRAGMENT = "search_results_fragment";
    public static final String TAG_SEARCH_COLLECTIONS_FRAGMENT = "search_collections_fragment";
    public static final String TAG_SEARCH_APPS_FRAGMENT = "search_apps_fragment";
    public static final String TAG_USER_PROFILE_FRAGMENT = "user_profile_fragment";
    public static final String TAG_INVITE_FRAGMENT = "invite_fragment";
    public static final String TAG_WELCOME_FRAGMENT = "fragment_welcome";
    public static final String TAG_FIND_FRIENDS_FRAGMENT = "fragment_find_friends";
    public static final String TAG_FIND_TWITTER_FRIENDS = "fragment_find_twitter_friends";
    public static final String TAG_FIND_FACEBOOK_FRIENDS = "fragment_find_facebook_friends";
    public static final String TAG_FOLLOWINGS_FRAGMENT = "fragment_followings_fragment";
    public static final String TAG_FOLLOWERS_FRAGMENT = "fragment_followers_fragment";

    // RequestCodes
    public static final int REQUEST_NETWORK_SETTINGS = 3;
    public static final int RC_INSTALL_SERVICE = 123;
    public static final int RC_DAILY_SERVICE = 124;

    //Extras
    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_SELECTED_IMAGE = "selected_image";
    public static final String EXTRA_APP_PACKAGE = "app_package";

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
    public static final int GPLUS_SIGN_IN = 0;
    public static final int FACEBOOK_SIGN_IN = 12;
    public static final int SHOW_LOGIN = 15;
    public static final int SHOW_INVITE = 16;

    public static final String DEFAULT_NOTIFICATION_TITLE = "Check out today's useful apps";
    public static final String DEFAULT_NOTIFICATION_MSG = "Which are the trending apps today?";
    public static final String FLURRY_API_KEY = "TF65K4T659FTCPXGMNG3";
    public static final String FLURRY_DEBUG_API_KEY = "TF65K4T659FTCPXGMNG4";
    public static final int SEARCH_RESULT_COUNT = 30;

    // GCM
    public static final String GCM_SENDER_ID = "437946264894";
//    public static final String TWITTER_CONSUMER_KEY = "ZQzGutRJY47XcrFB6XuCqdwAj";
//    public static final String TWITTER_CONSUMER_SECRET = "Fgt2TcaTJw9ceBC1uKZwG1vlqgz8s90sbSNkA635GEIdXygTbh";
    public static final String TWITTER_CONSUMER_KEY = "PLMXpFYLb7DxrpKFQQssnSYDI";
    public static final String TWITTER_CONSUMER_SECRET = "Ws9Z1ysjAwKjANHEZJncY92ayclpUmnZTUU1C5VNPkqim9fR8d";


    public static final int TRENDING_APPS = 1;
    public static final int TOP_HUNTERS = 2;
    public static final int TOP_APPS = 3;
    public static final int COLLECTIONS = 4;
    public static final int SUGGESTIONS = 6;
    public static final int SETTINGS = 7;
    public static final int HELP_ADD_APP =10;
    public static final int HELP_APPS_REQUIREMENTS = 11;
    public static final int HELP_TOP_HUNTERS_POINTS = 12;

    public static final int PAGE_SIZE = 5;
    public static final int COMMENTS_PAGE_SIZE = 10;
    public static final int MIN_COLLECTION_APPS_SIZE = 4;
    public static final int THIRTY_MINS = 30 * 60 * 1000;

    public enum ItemType {
        SEPARATOR(0), ITEM(1), MORE_APPS(2),
        COMMENT(3), SUBCOMMENT(4), COLLECTION(5);

        private int value;

        ItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum CollectionStatus {
        @SerializedName("draft")
        DRAFT("draft"),
        @SerializedName("public")
        PUBLIC("public");

        private String value;

        CollectionStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum LoginProviders {
        FACEBOOK(0), GOOGLEPLUS(1), TWITTER(2);

        private int value;

        LoginProviders(int value) { this.value = value; }

        public int getValue() {
            return value;
        }
    }

    // DeepLinking values
    public static final String KEY_DL_TYPE = "dl_type";
    public static final String KEY_SENDER_ID = "sender_id";
    public static final String KEY_SENDER_NAME = "sender_name";
    public static final String KEY_SENDER_PROFILE_IMAGE_URL = "sender_profile_image_url";
    public static final String KEY_RECEIVER_NAME = "receiver_name";

}
