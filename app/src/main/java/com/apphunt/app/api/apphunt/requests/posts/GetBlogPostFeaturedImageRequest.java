package com.apphunt.app.api.apphunt.requests.posts;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.posts.FeaturedImage;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.posts.GetFeaturedImageApiEvent;

public class GetBlogPostFeaturedImageRequest extends BaseGetRequest<FeaturedImage> {
    private final int postId;

    public GetBlogPostFeaturedImageRequest(int postId, int mediaId, Response.ErrorListener listener) {
        super(BLOG_URL + "/wp-json/wp/v2/media/" + mediaId, listener);
        this.postId = postId;
    }


    @Override
    public Class<FeaturedImage> getParsedClass() {
        return FeaturedImage.class;
    }

    @Override
    public void deliverResponse(FeaturedImage response) {
        BusProvider.getInstance().post(new GetFeaturedImageApiEvent(postId, response.getSourceUrl()));
    }
}
