package com.apphunt.app.event_bus.events.api.apps;

import com.apphunt.app.api.apphunt.models.tags.AppsSearchResult;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class AppsSearchResultEvent {

    private AppsSearchResult result;

    public AppsSearchResultEvent(AppsSearchResult result) {
        this.result = result;
    }

    public AppsSearchResult getResult() {
        return result;
    }

    public void setResult(AppsSearchResult result) {
        this.result = result;
    }
}
