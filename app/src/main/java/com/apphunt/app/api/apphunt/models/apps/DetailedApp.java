package com.apphunt.app.api.apphunt.models.apps;

import com.apphunt.app.api.apphunt.models.comments.Comments;

public class DetailedApp {
    private App baseApp;
    private Comments commentsData;

    public App getBaseApp() {
        return baseApp;
    }

    public void setBaseApp(App baseApp) {
        this.baseApp = baseApp;
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
                "app=" + baseApp +
                ", commentsData=" + commentsData +
                '}';
    }
}
