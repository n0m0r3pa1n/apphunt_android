package com.apphunt.app.api.apphunt.client;

import android.content.Context;

import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.api.apphunt.callback.Callback;
import com.apphunt.app.api.apphunt.models.AppsList;
import com.apphunt.app.api.apphunt.models.CommentVote;
import com.apphunt.app.api.apphunt.models.Comments;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.Notification;
import com.apphunt.app.api.apphunt.models.Packages;
import com.apphunt.app.api.apphunt.models.SaveApp;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.apphunt.models.Vote;
import com.apphunt.app.api.apphunt.requests.apps.GetAppDetailsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppsRequest;

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
    public void getApps(String userId, String date, int page, int pageSize, String platform) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetAppsRequest(date, userId, platform, pageSize, page));
    }

    @Override
    public void getDetailedApp(String userId, String appId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetAppDetailsRequest(appId, userId, null));
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
