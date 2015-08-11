package com.apphunt.app.event_bus.events.api.collections;

import com.apphunt.app.api.apphunt.models.tags.CollectionsSearchResult;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/10/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionsSearchResultEvent {

    private CollectionsSearchResult result;

    public CollectionsSearchResultEvent(CollectionsSearchResult result) {
        this.result = result;
    }

    public CollectionsSearchResult getResult() {
        return result;
    }

    public void setResult(CollectionsSearchResult result) {
        this.result = result;
    }
}
