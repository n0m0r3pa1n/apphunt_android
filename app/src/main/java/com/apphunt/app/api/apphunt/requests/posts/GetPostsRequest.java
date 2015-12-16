package com.apphunt.app.api.apphunt.requests.posts;

import android.util.Log;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.posts.BlogPost;
import com.apphunt.app.api.apphunt.requests.base.BaseGetRequest;
import com.apphunt.app.utils.GsonInstance;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by nmp on 15-12-16.
 */
public class GetPostsRequest extends BaseGetRequest<JsonArray> {
    public GetPostsRequest(int page, int pageSize,Response.ErrorListener listener) {
        super(BLOG_URL+ "/wp-json/wp/v2/posts?page=" + page + "&per_page=" + pageSize, listener);
    }

    @Override
    public Class<JsonArray> getParsedClass() {
        return JsonArray.class;
    }

    @Override
    public void deliverResponse(JsonArray response) {
        Log.d(TAG, "deliverResponse: " + response.toString());
        List<BlogPost> posts = GsonInstance.fromJson(response.toString(), new TypeToken<List<BlogPost>>(){}.getType());
        Log.d(TAG, "deliverResponse: ");
    }
}
