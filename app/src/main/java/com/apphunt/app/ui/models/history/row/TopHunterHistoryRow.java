package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.fragments.TopHuntersFragment;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;

public class TopHunterHistoryRow extends BaseHistoryRow {
    public TopHunterHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {
        activity.getSupportFragmentManager().beginTransaction()
                .add(new TopHuntersFragment(), Constants.TAG_TOP_HUNTERS_FRAGMENT)
                .commit();
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_notifications_user;
    }
}
