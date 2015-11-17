package com.apphunt.app.api.apphunt.models.notifications;


import java.util.HashMap;

public enum NotificationType {
    APP_APPROVED("appApproved"),
    APP_REJECTED("appRejected"),
    USER_COMMENT("userComment"),
    USER_MENTIONED("userMentioned"),
    TOP_HUNTER("topHunters"),
    TOP_APPS("topApps"),
    GENERIC("generic"),
    DAILY("daily"),
    INSTALL("install"),
    COMMENT_APP("comment_app");

    public final HashMap<NotificationType, Integer> requestCodes = new HashMap<NotificationType, Integer>() {{
        put(DAILY, 101);
        put(APP_APPROVED, 102);
        put(APP_REJECTED, 103);
        put(USER_COMMENT, 104);
        put(USER_MENTIONED, 105);
        put(TOP_HUNTER, 106);
        put(TOP_APPS, 107);
        put(GENERIC, 108);
        put(INSTALL, 109);
    }};

    private final String text;

    /**
     * @param text
     */
    NotificationType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    public int getRequestCode() {
        if(!requestCodes.containsKey(this)) {
            return 200;
        }

        return requestCodes.get(this);
    }

    public static NotificationType getType(String text) {
        for(NotificationType type : values()) {
            if(type.text.equals(text)) {
                return type;
            }
        }

        return NotificationType.GENERIC;
    }
}