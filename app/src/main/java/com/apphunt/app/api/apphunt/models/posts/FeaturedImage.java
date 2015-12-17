package com.apphunt.app.api.apphunt.models.posts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nmp on 15-12-17.
 */
public class FeaturedImage {
    @SerializedName("source_url")
    private String sourceUrl;

    public String getSourceUrl() {
        return sourceUrl;
    }
}
