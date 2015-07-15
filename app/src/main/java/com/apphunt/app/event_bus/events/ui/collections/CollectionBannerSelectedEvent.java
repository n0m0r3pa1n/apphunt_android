package com.apphunt.app.event_bus.events.ui.collections;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBannerSelectedEvent {

    private String banner;

    public CollectionBannerSelectedEvent(String banner) {
        this.banner = banner;
    }

    public String getBannerUrl() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
}
