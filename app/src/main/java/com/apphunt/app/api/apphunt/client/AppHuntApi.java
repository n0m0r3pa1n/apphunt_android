package com.apphunt.app.api.apphunt.client;

import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.apps.SaveApp;
import com.apphunt.app.api.apphunt.models.users.User;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;


public interface AppHuntApi {
    @POST("/users")
    void createUser(User user);

    @PUT("/users/{userId}")
    void updateUser(User user);

    @GET("/apps")
    void getApps(@Query("userId") String userId, @Query("date") String date, @Query("page") int page,
                 @Query("pageSize") int pageSize, @Query("platform") String platform);

    @GET("/apps/{appId}")
    void getDetailedApp(String userId, String appId);

    @POST("/apps/actions/filter")
    void filterApps(Packages packages);

    @POST("/apps/votes")
    void vote(@Query("appId") String appId, @Query("userId") String userId);

    @DELETE("/apps/votes")
    void downVote(String appId, String userId);

    @POST("/apps")
    void saveApp(SaveApp app);

    @GET("/apps/search")
    void searchApps(String query, String userId, int page,
                    int pageSize, String platform);

    @GET("/notifications")
    void getNotification(String type);

    @POST("/comments")
    void sendComment(NewComment comment);

    @GET("/comments/{appId}")
    void getAppComments(String appId, String userId, int page, int pageSize, boolean shouldReload);

    @POST("/comments/votes")
    void voteComment(String userId, String commentId);

    @DELETE("/comments/votes")
    void downVoteComment(String userId, String commentId);

    void getTopAppsCollection(String criteria, String userId);
}
