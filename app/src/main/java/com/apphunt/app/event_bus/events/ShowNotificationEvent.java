package com.apphunt.app.event_bus.events;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/21/15.
 */
public class ShowNotificationEvent {
    private String message;

    public ShowNotificationEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
