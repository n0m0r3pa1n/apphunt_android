package com.shtaigaway.apphunt.utils;


public class Constants {
    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    public static final String PLATFORM = "Android";

    public static final String IS_DAILY_NOTIFICATION_SETUP_KEY = "isDisplayNotificationServiceSetup";

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
