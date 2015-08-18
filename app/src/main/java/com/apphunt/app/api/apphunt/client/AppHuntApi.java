package com.apphunt.app.api.apphunt.client;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.Pagination;
import com.apphunt.app.api.apphunt.models.apps.Packages;
import com.apphunt.app.api.apphunt.models.apps.SaveApp;
import com.apphunt.app.api.apphunt.models.collections.NewCollection;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.models.comments.NewComment;
import com.apphunt.app.api.apphunt.models.users.User;

public interface AppHuntApi {
    void createUser(User user);

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

    void getNotification(String type);

    void sendComment(NewComment comment);

    void getAppComments(String appId, String userId, int page, int pageSize, boolean shouldReload);

    void voteComment(String userId, String commentId);

    void downVoteComment(String userId, String commentId);

    void createCollection(NewCollection collection);

    void getTopAppsCollection(String criteria, String userId);

    void getTopHuntersCollection(String criteria);

    void getFavouriteCollections(String userId, int page, int pageSize);

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

    void getUserProfile(String userId);

    void getUserComments(String creatorId, String userId, Pagination pagination);

    void getUserApps(String creatorId, String userId, Pagination pagination);
}