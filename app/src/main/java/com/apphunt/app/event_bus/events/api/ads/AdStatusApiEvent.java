package com.apphunt.app.event_bus.events.api.ads;

import com.apphunt.app.api.apphunt.models.ads.AdStatus;

/**
 * Created by nmp on 16-1-19.
 */
public class AdStatusApiEvent {
    private String appPackage;
    private AdStatus adStatus;

    public AdStatusApiEvent(AdStatus adStatus, String appPackage) {
        this.adStatus = adStatus;
        this.appPackage = appPackage;
    }

    public AdStatus getAdStatus() {
        return adStatus;
    }

    public String getAppPackage() {
        return appPackage;
    }
}
