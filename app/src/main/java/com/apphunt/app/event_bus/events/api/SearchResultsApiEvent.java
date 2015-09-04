package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.SearchItems;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class SearchResultsApiEvent {
    private SearchItems searchItems;

    public SearchResultsApiEvent(SearchItems searchItems) {
        this.searchItems = searchItems;
    }

    public SearchItems getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(SearchItems searchItems) {
        this.searchItems = searchItems;
    }

    @Override
    public String toString() {
        return "SearchResultsApiEvent{" +
                "searchItems=" + searchItems +
                '}';
    }
}
