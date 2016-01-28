package com.apphunt.app.api.apphunt.models.chat;

import com.apphunt.app.api.apphunt.models.users.User;

/**
 * Created by nmp on 16-1-27.
 */
public class ChatMessage {
    private String message;
    private User user;

    public ChatMessage(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
