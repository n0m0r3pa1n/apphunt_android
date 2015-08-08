package com.apphunt.app.ui.listview_items;

import com.apphunt.app.api.apphunt.models.collections.apps.AppsCollection;
import com.apphunt.app.constants.Constants;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/8/15.
 * *
 * * NaughtySpirit 2015
 */
public class CollectionItem implements Item {

    private Constants.ItemType type;
    private AppsCollection collection;

    public CollectionItem(AppsCollection collection) {
        this.collection = collection;
        this.type = Constants.ItemType.COLLECTION;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public AppsCollection getData() {
        return collection;
    }
}
