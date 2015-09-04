package com.apphunt.app.ui.listview_items;

import com.apphunt.app.api.apphunt.models.apps.BaseApp;
import com.apphunt.app.constants.Constants;

public class AppItem implements Item {

    private Constants.ItemType type;
    private BaseApp baseApp;

    public AppItem(BaseApp baseApp) {
        this.baseApp = baseApp;
        this.type = Constants.ItemType.ITEM;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public BaseApp getData() {
        return baseApp;
    }
}
