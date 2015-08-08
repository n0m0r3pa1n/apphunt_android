package com.apphunt.app.api.apphunt.models;

import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class SearchItems {
    private ArrayList<App> apps = new ArrayList<>();
    private ArrayList<AppsCollection> collections = new ArrayList<>();

    public ArrayList<AppsCollection> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<AppsCollection> collections) {
        this.collections = collections;
    }

    public ArrayList<App> getApps() {
        return apps;
    }

    public void setApps(ArrayList<App> apps) {
        this.apps = apps;
    }

    @Override
    public String toString() {
        return "SearchItems{" +
                "apps=" + apps +
                ", collections=" + collections +
                '}';
    }
}
