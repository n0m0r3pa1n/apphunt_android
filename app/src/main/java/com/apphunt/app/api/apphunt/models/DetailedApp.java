package com.apphunt.app.api.apphunt.models;

public class DetailedApp {
    private App app;
    private Comments commentsData;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Comments getCommentsData() {
        return commentsData;
    }

    public void setCommentsData(Comments commentsData) {
        this.commentsData = commentsData;
    }

    @Override
    public String toString() {
        return "DetailedApp{" +
                "app=" + app +
                ", commentsData=" + commentsData +
                '}';
    }
}
