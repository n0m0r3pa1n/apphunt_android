package com.shtaigaway.apphunt.utils;


public class Constants {
    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    public enum ItemType {
        SEPARATOR(0), ITEM(1);

        private int value;

        ItemType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
