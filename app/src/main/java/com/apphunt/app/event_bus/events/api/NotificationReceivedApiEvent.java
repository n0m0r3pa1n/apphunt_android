package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.notifications.Notification;

/**
 * Created by nmp on 15-5-12.
 */
public class NotificationReceivedApiEvent {
    private Notification notification;
    public NotificationReceivedApiEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}

