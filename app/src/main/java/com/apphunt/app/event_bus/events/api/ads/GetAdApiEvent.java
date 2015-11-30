package com.apphunt.app.event_bus.events.api.ads;

import com.apphunt.app.api.apphunt.models.ads.Ad;

/**
 * Created by nmp on 15-11-30.
 */
public class GetAdApiEvent {
    private Ad ad;

    public GetAdApiEvent(Ad ad) {
        this.ad = ad;
    }

    public Ad getAd() {
        return ad;
    }
}
