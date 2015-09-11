package com.apphunt.app.api.apphunt.models.notifications;

public class Notification {
    private String title;
    private String message;
    private String image;
    private NotificationType type;

    public Notification(String title, String message, String image, NotificationType type) {
        this.title = title;
        this.message = message;
        this.image = image;
        this.type = type;
    }

    public Notification() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
