package com.apphunt.app.event_bus.events.ui.collections;

import com.apphunt.app.api.apphunt.models.collections.apps.CollectionBanner;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBannerSelectedEvent {

    private CollectionBanner banner;

    public CollectionBannerSelectedEvent(CollectionBanner banner) {
        this.banner = banner;
    }

    public CollectionBanner getBanner() {
        return banner;
    }

    public void setBanner(CollectionBanner banner) {
        this.banner = banner;
    }
}
