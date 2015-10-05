package com.apphunt.app.ui.models.history.row;

import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.models.history.row.base.HistoryRowComponent;

public class HeaderHistoryRow implements HistoryRowComponent {
    private final String date;

    public HeaderHistoryRow(String date) {
        this.date = date;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void openEvent() {

    }

    @Override
    public void openUserProfile() {

    }

    @Override
    public boolean isUnseen() {
        return true;
    }

    @Override
    public void setIsUnseen(boolean isUnseen) {

    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public int getIconResId() {
        return 0;
    }

    @Override
    public String getText() {
        return date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public Constants.ItemType getType() {
        return Constants.ItemType.SEPARATOR;
    }
}
