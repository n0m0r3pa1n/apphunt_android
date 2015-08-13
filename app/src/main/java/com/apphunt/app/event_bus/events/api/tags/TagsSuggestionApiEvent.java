package com.apphunt.app.event_bus.events.api.tags;

import com.apphunt.app.api.apphunt.models.tags.Tags;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class TagsSuggestionApiEvent {

    private Tags tags;

    public TagsSuggestionApiEvent(Tags tags) {
        this.tags = tags;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }
}
