package com.apphunt.app.api.twitter;

import com.apphunt.app.api.twitter.models.Friends;
import com.apphunt.app.utils.StringUtils;
import com.twitter.sdk.android.core.Callback;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 3/6/15.
 */
public interface FriendsService {
    @GET("/1.1/friends/list.json")
    void getFriends(@Query("screen_name") String screenName, Callback<Friends> cb);

    @POST("/1.1/direct_messages/new.json")
    void sendDirectMessage(@Query("user_id") String userId, @Query("text") String text, Callback<Response> cb);
}
