package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;
import com.apphunt.app.utils.ui.NavUtils;

public class CommentHistoryRow extends BaseHistoryRow {
    public CommentHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {
        String appId = event.getParams().get("appId").getAsString();
        NavUtils.getInstance(activity).presentAppDetailsFragment(appId);
        NavUtils.getInstance(activity).presentCommentsFragment(appId);
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_notifications_comments;
    }
}
