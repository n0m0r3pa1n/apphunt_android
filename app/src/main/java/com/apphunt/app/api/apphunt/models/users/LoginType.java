package com.apphunt.app.api.apphunt.models.users;

/**
 * Created by nmp on 15-11-12.
 */
public enum LoginType {
    Facebook("facebook"),
    GooglePlus("google-plus"),
    Twitter("twitter"),
    Anonymous("anonymous");

    private final String text;

    /**
     * @param text
     */
    LoginType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
