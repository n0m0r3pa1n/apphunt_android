package com.apphunt.app.api.apphunt.clients.rest;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.apps.SaveApp;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.notifications.Notification;
import com.apphunt.app.api.apphunt.models.users.FollowingsIdsList;
import com.apphunt.app.api.apphunt.models.users.NamesList;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants.LoginProviders;
import com.google.gson.JsonArray;

import java.util.Date;
import java.util.List;

public interface AppHuntApi {
    void createUser(User user, Response.Listener<User> listener);

    void updateUser(User user);

    void getApps(String userId, String date, int page,
                 int pageSize, String platform);

    void getDetailedApp(String userId, String appId);

    void filterApps(Packages packages);

    void filterApps(Packages packages, Response.Listener<Packages> listener);

    void vote(String appId, String userId);

    void downVote(String appId, String userId);

    void saveApp(SaveApp app);

    void searchApps(String query, String userId, int page,
                    int pageSize, String platform);

    void getNotification(String type, Response.Listener<Notification> listener);

    void sendComment(NewComment comment);

    void getAppComments(String appId, String userId, int page, int pageSize, boolean shouldReload);

    void voteComment(String userId, String commentId);

    void downVoteComment(String userId, String commentId);

    void createCollection(NewCollection collection);

    void getTopAppsCollection(String criteria, String userId);

    void getTopHuntersCollection(String criteria);

    void getTopHuntersForCurrentMonth();

    void getFavouriteCollections(String favouritedBy, String userId, int page, int pageSize);

    void getAppCollection(String collectionId, String userId);

    void getAllCollections(String userId, String sortBy, int page, int pageSize);

    void voteCollection(String userId, String collectionId);

    void downVoteCollection(String userId, String collectionId);

    void getUserCollections(String creatorId, String userId, int page, int pageSize);

    void getMyAvailableCollections(String userId, String appId, int page, int pageSize);

    void favouriteCollection(AppsCollection collection, String userId);

    void unfavouriteCollection(String collectionId, String userId);

    void updateCollection(String userId, AppsCollection appsCollection);

    void deleteCollection(String collectionId);

    void getBanners();

    void getLatestAppVersionCode();

    void cancelAllRequests();

    void getTagsSuggestion(String str);

    void getItemsByTags(String tags, String userId);

    void getAppsByTags(String tags, int page, int pageSize, String userId);

    void getCollectionsByTags(String tags, int page, int pageSize, String userId);

    void getUserProfile(String profileId, String currentUserId, Date from, Date to);

    void getUserComments(String creatorId, String userId, Pagination pagination);

    void getUserApps(String creatorId, String userId, Pagination pagination);

    void favouriteApp(String appId, String userId);

    void unfavouriteApp(String appId, String userId);

    void getFavouriteApps(String favouritedBy, String userId, Pagination pagination);

    void getRandomApp(String userId);

    void getUserHistory(String userId, Date date);

    void filterFriends(String userId, NamesList names, LoginProviders provider);

    void followUser(String userId, String followingId);

    void followUsers(String userId, FollowingsIdsList followingIds);

    void unfollowUser(String userId, String followingId);

    void getFollowers(String profileId, String userId, int page, int pageSize);

    void getFollowings(String profileId, String userId, int page, int pageSize);

    void getAppsForPackages(List<String> packages, Response.Listener<JsonArray> listener);

    void getAd();
}