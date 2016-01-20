package com.apphunt.app.api.apphunt.models.ads;

/**
 * Created by nmp on 16-1-20.
 */
public class UserAdStatus {
    private boolean shouldShowAd;
    private String message;

    public UserAdStatus(boolean shouldShowAd, String message) {
        this.shouldShowAd = shouldShowAd;
        this.message = message;
    }

    public boolean shouldShowAd() {
        return shouldShowAd;
    }

    public String getMessage() {
        return message;
    }
}
