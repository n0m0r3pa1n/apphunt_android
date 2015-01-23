package com.shtaigaway.apphunt.app;

import com.shtaigaway.apphunt.api.models.App;
import com.shtaigaway.apphunt.utils.Constants;

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
