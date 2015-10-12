package com.apphunt.app.ui.models.history.row.base;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.ui.NavUtils;

public abstract class BaseHistoryRow implements HistoryRowComponent {
    protected final AppCompatActivity activity;
    protected final HistoryEvent event;
    private boolean isUnseen = false;

    public BaseHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        this.activity = activity;
        this.event = event;
    }

    @Override
    public String getId() {
        return event.getId();
    }

    @Override
    public void setIsUnseen(boolean isUnseen) {
        this.isUnseen = isUnseen;
    }

    @Override
    public Constants.ItemType getType() {
        return Constants.ItemType.ITEM;
    }

    @Override
    public String getText() {
        return event.getText();
    }

    @Override
    public User getUser() {
        return event.getUser();
    }

    @Override
    public void openUserProfile() {
        NavUtils.getInstance(activity).presentUserProfileFragment(event.getUser().getId(), event.getUser().getName());
    }

    @Override
    public String getDate() {
        return event.getDate();
    }

    @Override
    public boolean isUnseen() {
        return isUnseen;
    }

    @Override
    public boolean isFollowRow() {
        return event.getUser().isFollowing();
    }
}
