package com.apphunt.app.api.apphunt.models.tags;

import java.util.Arrays;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/7/15.
 * *
 * * NaughtySpirit 2015
 */
public class Tags {
    private String[] tags = {};

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Tags{" +
                "tags=" + Arrays.toString(tags) +
                '}';
    }
}
