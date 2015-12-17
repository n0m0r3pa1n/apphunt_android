package com.apphunt.app.event_bus.events.api.posts;

import com.apphunt.app.api.apphunt.models.posts.BlogPost;

import java.util.List;

/**
 * Created by nmp on 15-12-16.
 */
public class GetBlogPostApiEvent {
    private List<BlogPost> response;
    public GetBlogPostApiEvent(List<BlogPost> response) {
        this.response = response;
    }

    public List<BlogPost> getBlogPosts() {
        return response;
    }
}
