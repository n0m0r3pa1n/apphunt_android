package com.apphunt.app.api.apphunt.client;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.api.apphunt.models.NewComment;
import com.apphunt.app.api.apphunt.models.Packages;
import com.apphunt.app.api.apphunt.models.SaveApp;
import com.apphunt.app.api.apphunt.models.User;
import com.apphunt.app.api.apphunt.requests.GetNotificationRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppDetailsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetFilteredAppPackages;
import com.apphunt.app.api.apphunt.requests.apps.GetSearchedAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.PostAppRequest;
import com.apphunt.app.api.apphunt.requests.comments.GetAppCommentsRequest;
import com.apphunt.app.api.apphunt.requests.comments.PostNewCommentRequest;
import com.apphunt.app.api.apphunt.requests.users.PostUserRequest;
import com.apphunt.app.api.apphunt.requests.users.PutUserRequest;
import com.apphunt.app.api.apphunt.requests.votes.DeleteAppVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.DeleteCommentVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.PostAppVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.PostCommentVoteRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ApiErrorEvent;


public class AppHuntApiClient implements AppHuntApi {
    private Context context;
    public AppHuntApiClient(Context context) {
        this.context = context;
    }
    Response.ErrorListener listener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            BusProvider.getInstance().post(new ApiErrorEvent());
        }
    };


    @Override
    public void createUser(User user) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostUserRequest(user, listener));
    }

    @Override
    public void updateUser(String userId, User user) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PutUserRequest(userId, user, listener));
    }

    @Override
    public void getApps(String userId, String date, int page, int pageSize, String platform) {
        GetAppsRequest request;
        if(TextUtils.isEmpty(userId) ) {
            request = new GetAppsRequest(date, platform, pageSize, page);
        } else {
            request = new GetAppsRequest(date, userId, platform, pageSize, page);
        }
        VolleyInstance.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public void getDetailedApp(String userId, String appId) {
        GetAppDetailsRequest request;
        if(TextUtils.isEmpty(userId)) {
            request = new GetAppDetailsRequest(appId, listener);
        } else {
            request = new GetAppDetailsRequest(appId, userId,listener);
        }
        VolleyInstance.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public void filterApps(Packages packages) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetFilteredAppPackages(packages, listener));
    }

    @Override
    public void vote(String appId, String userId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostAppVoteRequest(appId, userId, null, listener));
    }

    @Override
    public void downVote(String appId, String userId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new DeleteAppVoteRequest(appId, userId, listener));
    }

    @Override
    public void saveApp(SaveApp app) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostAppRequest(app, listener));
    }

    @Override
    public void searchApps(String query, String userId, int page, int pageSize, String platform) {
        GetSearchedAppsRequest request;
        if(TextUtils.isEmpty(userId)) {
            request = new GetSearchedAppsRequest(query, page, pageSize, platform, listener);
        } else {
            request = new GetSearchedAppsRequest(query, userId, page, pageSize, platform, listener);
        }

        VolleyInstance.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public void getNotification(String type) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetNotificationRequest(type, listener));
    }

    @Override
    public void sendComment(NewComment comment) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostNewCommentRequest(comment, listener));
    }

    @Override
    public void getAppComments(String appId, String userId, int page, int pageSize, boolean shouldReload) {
        GetAppCommentsRequest request;

        if(TextUtils.isEmpty(userId)) {
            request = new GetAppCommentsRequest(appId, page, pageSize, shouldReload, listener);
        } else {
            request = new GetAppCommentsRequest(appId, userId, page, pageSize, shouldReload, listener);
        }
        VolleyInstance.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public void voteComment(String userId, String commentId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostCommentVoteRequest(commentId, userId, listener));
    }

    @Override
    public void downVoteComment(String userId,String commentId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new DeleteCommentVoteRequest(commentId, userId, listener));
    }
}
