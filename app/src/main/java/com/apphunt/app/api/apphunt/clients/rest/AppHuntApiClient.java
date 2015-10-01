package com.apphunt.app.api.apphunt.clients.rest;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apphunt.app.api.apphunt.VolleyInstance;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.apps.SaveApp;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.users.FollowingsList;
import com.apphunt.app.api.apphunt.models.users.NamesList;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.api.apphunt.requests.GetNotificationRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppDetailsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetFilteredAppPackages;
import com.apphunt.app.api.apphunt.requests.apps.GetRandomAppRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetSearchedAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.GetUserAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.PostAppRequest;
import com.apphunt.app.api.apphunt.requests.apps.favourite.FavouriteAppRequest;
import com.apphunt.app.api.apphunt.requests.apps.favourite.GetFavouriteAppsRequest;
import com.apphunt.app.api.apphunt.requests.apps.favourite.UnfavouriteAppRequest;
import com.apphunt.app.api.apphunt.requests.collections.DeleteCollectionRequest;
import com.apphunt.app.api.apphunt.requests.collections.FavouriteCollectionRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetAllCollectionsRequest;
import com.apphunt.app.api.apphunt.requests.collections.GetAppCollectionRequest;
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
import com.apphunt.app.api.apphunt.requests.comments.GetUserCommentsRequest;
import com.apphunt.app.api.apphunt.requests.comments.PostNewCommentRequest;
import com.apphunt.app.api.apphunt.requests.tags.GetAppsByTagsRequest;
import com.apphunt.app.api.apphunt.requests.tags.GetCollectionsByTagsRequest;
import com.apphunt.app.api.apphunt.requests.tags.GetItemsByTagsRequest;
import com.apphunt.app.api.apphunt.requests.tags.GetTagsSuggestionRequest;
import com.apphunt.app.api.apphunt.requests.users.GetFilterFriendsRequest;
import com.apphunt.app.api.apphunt.requests.users.GetUserHistoryRequest;
import com.apphunt.app.api.apphunt.requests.users.GetUserProfileRequest;
import com.apphunt.app.api.apphunt.requests.users.PostFollowUsersRequest;
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
import com.apphunt.app.constants.Constants.LoginProviders;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ApiErrorEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.apphunt.app.api.apphunt.requests.collections.PutCollectionsRequest.UpdateCollectionModel;


public class AppHuntApiClient implements AppHuntApi {
    public static final String TAG = AppHuntApiClient.class.getSimpleName();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");

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
    public void getNotification(String type, Response.Listener<Notification> callback) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetNotificationRequest(type, callback, listener));
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
    public void getFavouriteCollections(String favouritedBy, String userId, int page, int pageSize) {
        if(TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetFavouriteCollectionsRequest(favouritedBy, page, pageSize, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetFavouriteCollectionsRequest(favouritedBy, userId, page, pageSize, listener));
        }
    }

    @Override
    public void getAppCollection(String collectionId, String userId) {
        if(TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAppCollectionRequest(collectionId, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAppCollectionRequest(collectionId, userId, listener));
        }
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
    public void getUserCollections(String creatorId, String userId, int page, int pageSize) {
        if(!TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetMyCollectionsRequest(creatorId, userId, page, pageSize, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetMyCollectionsRequest(creatorId, page, pageSize, listener));
        }
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

    @Override
    public void getItemsByTags(String tags, String userId) {
        tags = getFormattedQuery(tags);
        if (TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetItemsByTagsRequest(tags, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetItemsByTagsRequest(tags, userId, listener));
        }
    }

    @Override
    public void getAppsByTags(String tags, int page, int pageSize, String userId) {
        tags = getFormattedQuery(tags);
        if (TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAppsByTagsRequest(tags, page, pageSize, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetAppsByTagsRequest(tags, page, pageSize, userId, listener));
        }
    }

    @Override
    public void getCollectionsByTags(String tags, int page, int pageSize, String userId) {
        tags = getFormattedQuery(tags);
        if (TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetCollectionsByTagsRequest(tags, page, pageSize, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetCollectionsByTagsRequest(tags, page, pageSize, userId, listener));
        }
    }

    @Override
    public void getUserProfile(String userId, Date fromDate, Date toDate) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String fromStr = dayFormat.format(fromDate);
        String toStr = dayFormat.format(toDate);
        if (LoginProviderFactory.get((Activity) context).isUserLoggedIn()) {
            String currentUserId = LoginProviderFactory.get((Activity) context).getUser().getId();
            VolleyInstance.getInstance(context).addToRequestQueue(new GetUserProfileRequest(userId, currentUserId, fromStr, toStr, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetUserProfileRequest(userId, fromStr, toStr, listener));
        }
    }

    @Override
    public void getUserComments(String creatorId, String userId, Pagination pagination) {
        GetUserCommentsRequest getUserCommentsRequest;
        if(!TextUtils.isEmpty(userId)) {
            getUserCommentsRequest = new GetUserCommentsRequest(creatorId, userId, pagination, listener);
        } else {
            getUserCommentsRequest = new GetUserCommentsRequest(creatorId, pagination, listener);
        }
        VolleyInstance.getInstance(context).addToRequestQueue(getUserCommentsRequest);
    }

    @Override
    public void getUserApps(String creatorId, String userId, Pagination pagination) {
        GetUserAppsRequest getUserAppsRequest;
        if(!TextUtils.isEmpty(userId)) {
            getUserAppsRequest = new GetUserAppsRequest(creatorId, userId, pagination, listener);
        } else {
            getUserAppsRequest = new GetUserAppsRequest(creatorId, pagination, listener);
        }
        VolleyInstance.getInstance(context).addToRequestQueue(getUserAppsRequest);
    }

    @Override
    public void favouriteApp(String appId, String userId) {
         VolleyInstance.getInstance(context).addToRequestQueue(new FavouriteAppRequest(appId, userId, listener));
    }

    @Override
    public void unfavouriteApp(String appId, String userId) {
        VolleyInstance.getInstance(context).addToRequestQueue(new UnfavouriteAppRequest(appId, userId, listener));
    }

    @Override
    public void getFavouriteApps(String favouritedBy, String userId, Pagination pagination) {
        if(TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetFavouriteAppsRequest(favouritedBy, pagination, listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetFavouriteAppsRequest(favouritedBy, userId, pagination, listener));
        }
    }

    @Override
    public void getRandomApp(String userId) {
        if(TextUtils.isEmpty(userId)) {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetRandomAppRequest(listener));
        } else {
            VolleyInstance.getInstance(context).addToRequestQueue(new GetRandomAppRequest(userId, listener));
        }
    }

    @Override
    public void filterFriends(String userId, NamesList names, LoginProviders provider) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetFilterFriendsRequest(userId, names, provider, listener));
    }

    @Override
    public void followUser(String userId, String followingId) {

    }

    @Override
    public void followUsers(String userId, FollowingsList followingIds) {
        VolleyInstance.getInstance(context).addToRequestQueue(new PostFollowUsersRequest(userId, followingIds, listener));
    }

    @Override
    public void unfollowUser(String userId, String followingId) {

    }

    @Override
    public void getFollowers(String userId, int page, int pageSize) {

    }

    @Override
    public void getFollowings(String userId, int page, int pageSize) {
    
    }

    @Override
    public void getUserHistory(String userId, Date date) {
        VolleyInstance.getInstance(context).addToRequestQueue(new GetUserHistoryRequest(userId, dateFormat.format(date), listener));
    }

    private String getFormattedQuery(String q) {
        String[] tags = q.split(" ");
        String query = "?";

        for (String tag : tags) {
            query += "names[]=" + tag + "&";
        }

        return query;
    }
}
