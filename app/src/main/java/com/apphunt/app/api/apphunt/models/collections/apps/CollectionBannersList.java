package com.apphunt.app.api.apphunt.models.collections.apps;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBannersList {
    private ArrayList<CollectionBanner> banners = new ArrayList<>();

    public ArrayList<CollectionBanner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<CollectionBanner> banners) {
        this.banners = banners;
    }

    @Override
    public String toString() {
        return "CollectionBannersList{" +
                "banners=" + banners +
                '}';
    }
}
