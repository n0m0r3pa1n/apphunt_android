package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.User;

public class UserCreatedApiEvent {
    private User user;
    public UserCreatedApiEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
