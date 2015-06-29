package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.notifications.Notification;

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

