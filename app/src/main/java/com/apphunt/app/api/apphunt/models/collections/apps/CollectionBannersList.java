package com.apphunt.app.api.apphunt.models.collections.apps;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionBannersList {
    private ArrayList<String> banners = new ArrayList<>();

    public ArrayList<String> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<String> banners) {
        this.banners = banners;
    }

    @Override
    public String toString() {
        return "CollectionBannersList{" +
                "banners=" + banners +
                '}';
    }
}
