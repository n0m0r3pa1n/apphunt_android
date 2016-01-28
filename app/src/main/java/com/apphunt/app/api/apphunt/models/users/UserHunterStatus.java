package com.apphunt.app.api.apphunt.models.users;

/**
 * Created by nmp on 16-1-27.
 */
public class UserHunterStatus {
    private boolean isTopHunter;

    public UserHunterStatus(boolean isTopHunter) {
        this.isTopHunter = isTopHunter;
    }

    public boolean isTopHunter() {
        return isTopHunter;
    }
}
