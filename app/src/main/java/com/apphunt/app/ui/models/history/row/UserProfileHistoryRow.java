package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;
import com.apphunt.app.utils.ui.NavUtils;

public class UserProfileHistoryRow extends BaseHistoryRow {
    public UserProfileHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {
        NavUtils.getInstance(activity).presentUserProfileFragment(event.getUser().getId(), event.getUser().getName());
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_notifications_user;
    }
}
