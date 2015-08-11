package com.apphunt.app.api.apphunt.models.tags;

import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.api.apphunt.models.apps.AppsList;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsSearchResult extends AppsList {

    private ArrayList<App> results = new ArrayList<>();

    public ArrayList<App> getResults() {
        return results;
    }

    public void setResults(ArrayList<App> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "AppsSearchResult{" +
                "results=" + results +
                '}';
    }
}
