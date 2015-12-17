package com.apphunt.app.api.apphunt.models.posts;

import com.apphunt.app.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nmp on 15-12-16.
 */
public class BlogPost {


    private Title title;
    private Excerpt excerpt;
    private String link, date;
    private int id;

    @SerializedName("featured_image")
    private int featuredImage;

    private String featuredImageUrl;

    public String getTitle() {
        return title.getRendered();
    }

    public String getExcerpt() {
        return excerpt.getRendered().replace("[&hellip;]", "");
    }

    public String getLink() {
        return link;
    }

    public String getDate() {
        return StringUtils.getDateAsTitleString(date.substring(0, 10));
    }

    public int getFeaturedImage() {
        return featuredImage;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public int getId() {
        return id;
    }
}
