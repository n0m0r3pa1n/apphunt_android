package com.apphunt.app.ui.models.history.row.base;

import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;

public interface HistoryRowComponent {
    String getId();
    String getDate();
    void openEvent();
    void openUserProfile();
    int getIconResId();
    String getText();
    Constants.ItemType getType();
    User getUser();

    boolean isUnseen();
    boolean isFollowRow();
    void setIsUnseen(boolean isUnseen);
}
