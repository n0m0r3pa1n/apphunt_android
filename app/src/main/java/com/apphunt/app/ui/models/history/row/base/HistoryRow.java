package com.apphunt.app.ui.models.history.row.base;

import com.apphunt.app.api.apphunt.models.users.User;

public interface HistoryRow {
    void openEvent();
    void openUserProfile();
    User getUser();
    int getIconResId();
    String getText();
}
