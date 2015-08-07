package com.apphunt.app.api.apphunt.client;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.apps.SaveApp;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.GetNotificationRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppDetailsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetFilteredAppPackages;
import com.apphunt.app.api.apphunt.requests.apps.GetSearchedAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.PostAppRequest;
import com.apphunt.app.api.apphunt.requests.collections.DeleteCollectionRequest;
import com.apphunt.app.api.apphunt.requests.collections.FavouriteCollectionRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetAllCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetCollectionBannersRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetFavouriteCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetMyAvailableCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetMyCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetTopAppsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetTopHuntersRequest;
import com.apphunt.app.api.apphunt.requests.collections.PostCollectionRequest;
import com.apphunt.app.api.apphunt.requests.collections.PutCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.UnfavouriteCollectionRequest;
import com.apphunt.app.api.apphunt.requests.comments.GetAppCommentsRequest;
import com.apphunt.app.api.apphunt.requests.comments.PostNewCommentRequest;
import com.apphunt.app.api.apphunt.requests.tags.GetTagsSuggestionRequest;
import com.apphunt.app.api.apphunt.requests.users.PostUserRequest;
import com.apphunt.app.api.apphunt.requests.users.PutUserRequest;
import com.apphunt.app.api.apphunt.requests.version.GetLatestAppVersionRequest;
import com.apphunt.app.api.apphunt.requests.votes.DeleteAppVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.DeleteCollectionVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.DeleteCommentVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.PostAppVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.PostCollectionVoteRequest;
import com.apphunt.app.api.apphunt.requests.votes.PostCommentVoteRequest;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ApiErrorEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apphunt.app.api.apphunt.requests.collections.PutCollectionsRequest.UpdateCollectionModel;


public class AppHuntApiClient implements AppHuntApi {
    public static final String TAG = AppHuntApiClient.class.getSimpleName();

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
    public void updateUser(User user) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PutUserRequest(user, listener));
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
    public void filterApps(Packages packages, Response.Listener<Packages> successListener) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetFilteredAppPackages(packages, successListener, listener));
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
    public void downVoteComment(String userId, String commentId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new DeleteCommentVoteRequest(commentId, userId, listener));
    }

    @Override
    public void createCollection(NewCollection collection) {
        VolleyInstance.getInstance(context).addToRequestQueue(
                new PostCollectionRequest(collection, listener));
    }

    @Override
    public void getTopAppsCollection(String criteria, String userId) {
        if (LoginProviderFactory.get((Activity) context).isUserLoggedIn()) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetTopAppsRequest(criteria, userId, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetTopAppsRequest(criteria, listener));
        }
    }

    @Override
    public void getTopHuntersCollection(String criteria) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetTopHuntersRequest(criteria, listener));
    }

    @Override
    public void getFavouriteCollections(String userId, int page, int pageSize) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetFavouriteCollectionsRequest(userId, page, pageSize, listener));
    }

    @Override
    public void getAllCollections(String userId, String sortBy, int page, int pageSize) {
        if(TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAllCollectionsRequest(sortBy, page, pageSize, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAllCollectionsRequest(userId, sortBy, page, pageSize, listener));
        }
    }

    @Override
    public void voteCollection(String userId, String collectionId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostCollectionVoteRequest(collectionId, userId, listener));
    }

    @Override
    public void downVoteCollection(String userId, String collectionId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new DeleteCollectionVoteRequest(collectionId, userId, listener));
    }

    @Override
    public void getMyCollections(String userId, int page, int pageSize) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetMyCollectionsRequest(userId, page, pageSize, listener));
    }

    @Override
    public void getMyAvailableCollections(String userId, String appId, int page, int pageSize) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetMyAvailableCollectionsRequest(userId, appId, page, pageSize, listener));
    }

    @Override
    public void favouriteCollection(AppsCollection collection, String userId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new FavouriteCollectionRequest(collection, userId, listener));
    }

    @Override
    public void unfavouriteCollection(String collectionId, String userId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new UnfavouriteCollectionRequest(collectionId, userId, listener));
    }

    @Override
    public void updateCollection(String userId, AppsCollection appsCollection) {
        List<String> appIds = new ArrayList<>();
        for (BaseApp baseApp : appsCollection.getApps()) {
            appIds.add(baseApp.getId());
        }

        Map<String, UpdateCollectionModel> map = new HashMap<>();
        map.put("collection", new UpdateCollectionModel(appIds, appsCollection.getName(),
                appsCollection.getDescription(), appsCollection.getPicture()));

        VolleyInstance.getInstance(context).addToRequestQueue(new PutCollectionsRequest(appsCollection.getId(), userId,
                map, listener));
    }

    @Override
    public void deleteCollection(String collectionId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new DeleteCollectionRequest(collectionId, listener));
    }

    @Override
    public void getBanners() {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetCollectionBannersRequest(listener));
    }

    @Override
    public void getLatestAppVersionCode() {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetLatestAppVersionRequest(listener));
    }

    @Override
    public void cancelAllRequests() {
        VolleyInstance.getInstance(context).cancelAllRequests();
    }

    @Override
    public void getTagsSuggestion(String str) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetTagsSuggestionRequest(str, listener));
    }
}
