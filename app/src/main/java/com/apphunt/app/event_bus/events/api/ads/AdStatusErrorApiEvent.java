package com.apphunt.app.event_bus.events.api.ads;

public class AdStatusErrorApiEvent {
    private String appPackage;

    public AdStatusErrorApiEvent(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppPackage() {
        return appPackage;
    }
}
