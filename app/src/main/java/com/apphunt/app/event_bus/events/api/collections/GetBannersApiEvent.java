package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBannersList;

/**
 * Created by nmp on 15-7-9.
 */
public class GetBannersApiEvent {
    private CollectionBannersList bannersList;

    public GetBannersApiEvent(CollectionBannersList bannersList) {
        this.bannersList = bannersList;
    }

    public CollectionBannersList getBannersList() {
        return bannersList;
    }
}
