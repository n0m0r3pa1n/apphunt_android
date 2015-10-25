package com.apphunt.app.event_bus.events.ui;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/21/15.
 */
public class ShowNotificationEvent {
    private String message;
    private boolean popBackStack = true;
    private boolean showRating = true;
    public ShowNotificationEvent(String message) {
        this.message = message;
    }

    public ShowNotificationEvent(String message, boolean showRating, boolean popBackStack) {
        this.message = message;
        this.showRating = showRating;
        this.popBackStack = popBackStack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean shouldShowRating() {
        return showRating;
    }

    public boolean shouldPopBackStack() {
        return popBackStack;
    }
}
