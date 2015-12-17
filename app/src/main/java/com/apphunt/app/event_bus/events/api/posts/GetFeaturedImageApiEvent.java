package com.apphunt.app.event_bus.events.api.posts;

/**
 * Created by nmp on 15-12-17.
 */
public class GetFeaturedImageApiEvent {
    private int postId;
    private String imageUrl;

    public GetFeaturedImageApiEvent(int postId, String imageUrl) {
        this.postId = postId;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getPostId() {
        return postId;
    }
}
