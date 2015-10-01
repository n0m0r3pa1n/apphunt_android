package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.HistoryEventType;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;
import com.apphunt.app.utils.ui.NavUtils;

/**
 * Created by nmp on 15-9-29.
 */
public class AppHistoryRow extends BaseHistoryRow {
    public AppHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {
        NavUtils.getInstance(activity).presentAppDetailsFragment(event.getParams().get("appId").getAsString());
    }

    @Override
    public int getIconResId() {
        if(event.getType() == HistoryEventType.APP_APPROVED) {
            return R.drawable.ic_notifications_apps_approve;
        }

        return R.drawable.ic_notifications_apps;

    }
}
