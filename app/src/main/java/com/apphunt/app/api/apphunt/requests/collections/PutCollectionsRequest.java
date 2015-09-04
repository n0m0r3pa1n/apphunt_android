package com.apphunt.app.api.apphunt.requests.collections;

import com.android.volley.Response;
import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.api.apphunt.requests.base.BasePutRequest;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.collections.UpdateCollectionApiEvent;

import java.util.List;
import java.util.Map;

public class PutCollectionsRequest extends BasePutRequest<AppsCollection> {
    public PutCollectionsRequest(String collectionId, String userId, Map<String, UpdateCollectionModel> body, Response.ErrorListener listener) {
        super(BASE_URL + "/app-collections/" + collectionId +"?userId="  + userId, body, listener);
    }

    @Override
    public Class<AppsCollection> getParsedClass() {
        return AppsCollection.class;
    }

    @Override
    public void deliverResponse(AppsCollection response) {
        BusProvider.getInstance().post(new UpdateCollectionApiEvent(response));
    }

    public static class UpdateCollectionModel {
        private List<String> apps;
        private String name, description, picture;

        public UpdateCollectionModel(List<String> apps, String name, String description, String picture) {
            this.apps = apps;
            this.name = name;
            this.description = description;
            this.picture = picture;
        }

        public List<String> getApps() {
            return apps;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getPicture() {
            return picture;
        }
    }
}
