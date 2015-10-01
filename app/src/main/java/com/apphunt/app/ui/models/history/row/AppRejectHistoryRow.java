package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;

/**
 * Created by nmp on 15-9-29.
 */
public class AppRejectHistoryRow extends BaseHistoryRow {
    public AppRejectHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {

    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_notifications_apps_reject;
    }
}
