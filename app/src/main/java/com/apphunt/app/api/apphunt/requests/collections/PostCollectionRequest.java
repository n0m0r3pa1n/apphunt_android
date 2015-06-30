package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BasePostRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.CreateCollectionEvent;

/**
 * Created by nmp on 15-6-30.
 */
public class PostCollectionRequest extends BasePostRequest<AppsCollection> {
    public PostCollectionRequest(String userId, String name, String description, String pictureUrl, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections", new NewCollection(userId, name, description, pictureUrl), listener);
    }

    @Override
    public Class<AppsCollection> getParsedClass() {
        return AppsCollection.class;
    }

    @Override
    public void deliverResponse(AppsCollection response) {
        BusProvider.getInstance().post(new CreateCollectionEvent(response));
    }

    static class NewCollection {
        String userId;
        String name;
        String description;
        String pictureUrl;

        public NewCollection(String userId, String name, String description, String pictureUrl) {
            this.userId = userId;
            this.name = name;
            this.description = description;
            this.pictureUrl = pictureUrl;
        }
    }
}
