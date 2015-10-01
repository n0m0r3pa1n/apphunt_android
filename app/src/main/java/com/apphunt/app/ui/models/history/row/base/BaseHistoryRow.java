package com.apphunt.app.ui.models.history.row.base;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.utils.ui.NavUtils;

public abstract class BaseHistoryRow implements HistoryRow {
    protected final AppCompatActivity activity;
    protected final HistoryEvent event;

    public BaseHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        this.activity = activity;
        this.event = event;
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

}
