package com.apphunt.app.ui.listview_items;

import com.apphunt.app.api.models.App;
import com.apphunt.app.utils.Constants;

public class AppItem implements Item {

    private Constants.ItemType type;
    private App app;

    public AppItem(App app) {
        this.app = app;
        this.type = Constants.ItemType.ITEM;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public App getData() {
        return app;
    }
}
