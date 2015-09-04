package com.apphunt.app.api.apphunt.models.comments;

import com.apphunt.app.api.apphunt.models.apps.BaseApp;

public class ProfileComment extends Comment {
    private BaseApp app;

    public BaseApp getApp() {
        return app;
    }

    public void setApp(BaseApp app) {
        this.app = app;
    }
}
