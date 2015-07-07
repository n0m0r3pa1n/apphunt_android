package com.apphunt.app.api.apphunt.models.collections.apps;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBanner {
    private String bannerUrl;

    public CollectionBanner() {}

    public CollectionBanner(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    @Override
    public String toString() {
        return "CollectionBanner{" +
                "bannerUrl='" + bannerUrl + '\'' +
                '}';
    }
}
