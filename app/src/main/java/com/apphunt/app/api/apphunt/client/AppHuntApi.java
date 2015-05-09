package com.apphunt.app.api.apphunt.client;

import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.Notification;
import com.apphunt.app.api.apphunt.models.Packages;
import com.apphunt.app.api.apphunt.models.SaveApp;
import com.apphunt.app.api.apphunt.models.User;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;


public interface AppHuntApi {
    @POST("/users")
    void createUser(@Body User user, Callback<User> cb);

    @PUT("/users/{userId}")
    void updateUser(@Path("userId") String userId, @Body User user, Callback<User> cb);

    @GET("/apps")
    void getApps(@Query("userId") String userId, @Query("date") String date, @Query("page") int page,
                 @Query("pageSize") int pageSize, @Query("platform") String platform);

    @GET("/apps/{appId}")
    void getDetailedApp(String userId, String appId);

    @POST("/apps/actions/filter")
    void filterApps(@Body Packages packages, Callback<Packages> cb);

    @POST("/apps/votes")
    void vote(@Query("appId") String appId, @Query("userId") String userId);

    @DELETE("/apps/votes")
    void downVote(String appId, String userId);

    @POST("/apps")
    void saveApp(@Body SaveApp app, Callback cb);

    @GET("/apps/search")
    void searchApps(@Query("q") String query, @Query("userId") String userId, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb);

    @GET("/notifications")
    void getNotification(@Query("type") String type, Callback<Notification> cb);

    @POST("/comments")
    void sendComment(NewComment comment);

    @GET("/comments/{appId}")
    void getAppComments(String appId, String userId, int page, int pageSize, boolean shouldReload);

    @POST("/comments/votes")
    void voteComment(String userId, String commentId);

    @DELETE("/comments/votes")
    void downVoteComment(String userId, String commentId);
}
