package com.apphunt.app.ui.listview_items;

import com.apphunt.app.constants.Constants;

/**
 * Created by nmp on 15-11-30.
 */
public class AppHuntAdItem implements Item {
    @Override
    public Constants.ItemType getType() {
        return Constants.ItemType.APPHUNT_AD;
    }
}
