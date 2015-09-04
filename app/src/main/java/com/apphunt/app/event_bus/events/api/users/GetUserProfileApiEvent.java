package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.UserProfile;

/**
 * Created by nmp on 15-8-17.
 */
public class GetUserProfileApiEvent {
    private UserProfile userProfile;

    public GetUserProfileApiEvent(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}
