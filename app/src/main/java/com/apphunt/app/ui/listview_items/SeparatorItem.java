package com.apphunt.app.ui.listview_items;

import com.apphunt.app.constants.Constants;

public class SeparatorItem implements Item {

    private Constants.ItemType type;
    private String title;

    public SeparatorItem(String title) {
        this.type = Constants.ItemType.SEPARATOR;
        this.title = title;
    }

    public String getData() {
        return title;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }
}
