package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.users.User;

public class AnonymousUserCreatedApiEvent {
    private User user;
    public AnonymousUserCreatedApiEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
