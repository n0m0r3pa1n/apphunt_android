package com.apphunt.app.api.apphunt.client;

import android.content.Context;

import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.App;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.models.CommentVote;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.Notification;
import com.apphunt.app.api.apphunt.models.Packages;
import com.apphunt.app.api.apphunt.models.SaveApp;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.api.apphunt.requests.GetAppsRequest;

import retrofit.http.Body;
import retrofit.http.Path;
import retrofit.http.Query;


public class AppHuntApiClient implements AppHuntApi {
    private Context context;
    public AppHuntApiClient(Context context) {
        this.context = context;
    }

    @Override
    public void createUser(@Body User user, Callback<User> cb) {

    }

    @Override
    public void updateUser(@Path("userId") String userId, @Body User user, Callback<User> cb) {

    }

    @Override
    public void getApps(@Query("userId") String userId, @Query("date") String date, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetAppsRequest("2015-3-19", "Android", 5, 1));
    }

    @Override
    public void getDetailedApp(@Query("userId") String userId, @Path("appId") String appId, Callback<App> cb) {

    }

    @Override
    public void filterApps(@Body Packages packages, Callback<Packages> cb) {

    }

    @Override
    public void vote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb) {

    }

    @Override
    public void downVote(@Query("appId") String appId, @Query("userId") String userId, Callback<Vote> cb) {

    }

    @Override
    public void saveApp(@Body SaveApp app, Callback cb) {

    }

    @Override
    public void searchApps(@Query("q") String query, @Query("userId") String userId, @Query("page") int page, @Query("pageSize") int pageSize, @Query("platform") String platform, Callback<AppsList> cb) {

    }

    @Override
    public void getNotification(@Query("type") String type, Callback<Notification> cb) {

    }

    @Override
    public void sendComment(@Body NewComment comment, Callback<NewComment> cb) {

    }

    @Override
    public void getAppComments(@Path("appId") String appId, @Query("userId") String userId, @Query("page") int page, @Query("pageSize") int pageSize, Callback<Comments> cb) {

    }

    @Override
    public void voteComment(@Query("userId") String userId, @Query("commentId") String commentId, Callback<CommentVote> cb) {

    }

    @Override
    public void downVoteComment(@Query("userId") String userId, @Query("commentId") String commentId, Callback<CommentVote> cb) {

    }
}
