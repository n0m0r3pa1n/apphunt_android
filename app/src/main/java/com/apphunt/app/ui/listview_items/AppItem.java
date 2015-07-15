package com.apphunt.app.ui.listview_items;

import com.apphunt.app.api.apphunt.models.apps.App;
import com.apphunt.app.constants.Constants;

public class AppItem implements Item {

    private Constants.ItemType type;
    private App baseApp;

    public AppItem(App baseApp) {
        this.baseApp = baseApp;
        this.type = Constants.ItemType.ITEM;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public App getData() {
        return baseApp;
    }
}
