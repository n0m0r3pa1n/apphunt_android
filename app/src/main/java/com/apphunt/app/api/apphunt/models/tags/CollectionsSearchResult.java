package com.apphunt.app.api.apphunt.models.tags;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionsSearchResult extends AppsCollections {

    private ArrayList<AppsCollection> results = new ArrayList<>();

    public CollectionsSearchResult(List<AppsCollection> collections) {
        super(collections);
    }

    public ArrayList<AppsCollection> getResults() {
        return results;
    }

    public void setResults(ArrayList<AppsCollection> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "CollectionsSearchResult{" +
                "results=" + results +
                '}';
    }
}
