package com.apphunt.app.api;

import com.apphunt.app.api.models.AppsList;
import com.apphunt.app.api.models.DetailedApp;
import com.apphunt.app.api.models.Notification;
import com.apphunt.app.api.models.Packages;
import com.apphunt.app.api.models.SaveApp;
import com.apphunt.app.api.models.User;
import com.apphunt.app.api.models.Vote;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public interface AppHuntApi {
    @POST("/users")
    void createUser(@Body User user, Callback<User> cb);

    @GET("/apps")
    void getApps(@Query("userId") String userId, @Query("date") String date, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb);
    
    @GET("/apps/{appId}")
    void getDetailedApp(@Query("userId") String userId, @Path("appId") String appId, Callback<DetailedApp> cb);

    @POST("/apps/actions/filter")
    void filterApps(@Body Packages packages, Callback<Packages> cb);

    @POST("/apps/votes")
    void vote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb);

    @DELETE("/apps/votes")
    void downVote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb);

    @POST("/apps")
    void saveApp(@Body SaveApp app, Callback cb);

    @GET("/notifications")
    void getNotification(@Query("type") String type, Callback<Notification> cb);
}
