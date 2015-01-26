package com.shtaigaway.apphunt.api;

import com.shtaigaway.apphunt.api.models.AppsList;
import com.shtaigaway.apphunt.api.models.Notification;
import com.shtaigaway.apphunt.api.models.User;
import com.shtaigaway.apphunt.api.models.Vote;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface AppHuntApi {
    @POST("/users")
    void createUser(@Body User user, Callback<Response> cb);

    @GET("/apps")
    void getApps(@Query("date") String date, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb);

    @POST("/apps/{appId}/votes")
    void vote(@Path("appId") String appId, @Body Vote user, Callback<Vote> cb);

    @GET("/notifications")
    void getNotification(@Query("type") String type, Callback<Notification> cb);
}
