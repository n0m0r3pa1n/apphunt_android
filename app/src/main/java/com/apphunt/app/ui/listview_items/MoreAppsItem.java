package com.apphunt.app.ui.listview_items;


import com.apphunt.app.utils.Constants;

public class MoreAppsItem implements Item {

    private Constants.ItemType type;
    private int page;
    private int items;
    private String date;

    public MoreAppsItem(int page, int items, String date) {
        this.items = items;
        this.page = page;
        this.date = date;
        this.type = Constants.ItemType.MORE_APPS;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public int getNextPage() {
        return page + 1;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
