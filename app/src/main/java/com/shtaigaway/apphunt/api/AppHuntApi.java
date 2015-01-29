package com.shtaigaway.apphunt.api;

import com.shtaigaway.apphunt.api.models.App;
import com.shtaigaway.apphunt.api.models.AppsList;
import com.shtaigaway.apphunt.api.models.Notification;
import com.shtaigaway.apphunt.api.models.SaveApp;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.api.models.Vote;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;


public interface AppHuntApi {
    @POST("/users")
    void createUser(@Body User user, Callback<User> cb);

    @GET("/apps")
    void getApps(@Query("userId") String userId, @Query("date") String date, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb);

    @POST("/apps/votes")
    void vote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb);

    @DELETE("/apps/votes")
    void downVote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb);

    @POST("/apps")
    void saveApp(@Body SaveApp app, Callback cb);

    @GET("/notifications")
    void getNotification(@Query("type") String type, Callback<Notification> cb);
}
