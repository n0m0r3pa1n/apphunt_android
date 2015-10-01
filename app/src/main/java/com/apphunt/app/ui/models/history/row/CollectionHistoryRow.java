package com.apphunt.app.ui.models.history.row;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.row.base.BaseHistoryRow;
import com.apphunt.app.utils.ui.NavUtils;

public class CollectionHistoryRow extends BaseHistoryRow {
    public CollectionHistoryRow(AppCompatActivity activity, HistoryEvent event) {
        super(activity, event);
    }

    @Override
    public void openEvent() {
        NavUtils.getInstance(activity).presentViewCollectionFragment(event.getParams().get("collectionId").getAsString());
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_notifications_collection;
    }
}
