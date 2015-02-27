package com.apphunt.app.api;

import com.apphunt.app.api.models.AppsList;
import com.apphunt.app.api.models.Comment;
import com.apphunt.app.api.models.CommentVote;
import com.apphunt.app.api.models.Comments;
import com.apphunt.app.api.models.DetailedApp;
import com.apphunt.app.api.models.NewComment;
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
    void getDetailedApp(@Query("userId") String userId, @Path("appId") String appId, @Query("commentsCount") int commentsCount, Callback<DetailedApp> cb);

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
    
    @POST("/comments")
    void sendComment(@Body NewComment comment, Callback<NewComment> cb);
    
    @GET("/comments/{appId}")
    void getAppComments(@Path("appId") String appId, @Query("userId") String userId, @Query("page") int page, @Query("pageSize") int pageSize, Callback<Comments> cb);
    
    @POST("/comments/votes")
    void voteComment(@Query("userId") String userId, @Query("commentId") String commentId, Callback<CommentVote> cb);

    @DELETE("/comments/votes")
    void downVoteComment(@Query("userId") String userId, @Query("commentId") String commentId, Callback<CommentVote> cb);
}
