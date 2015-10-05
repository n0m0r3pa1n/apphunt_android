package com.apphunt.app.ui.models.history;

import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.ui.models.history.row.AppHistoryRow;
import com.apphunt.app.ui.models.history.row.AppRejectHistoryRow;
import com.apphunt.app.ui.models.history.row.CollectionHistoryRow;
import com.apphunt.app.ui.models.history.row.CommentHistoryRow;
import com.apphunt.app.ui.models.history.row.TopHunterHistoryRow;
import com.apphunt.app.ui.models.history.row.UserProfileHistoryRow;
import com.apphunt.app.ui.models.history.row.base.HistoryRowComponent;

public class HistoryRowBuilder {
    public static HistoryRowComponent build(AppCompatActivity activity, String date, HistoryEvent event) {
        event.setDate(date);
        switch(event.getType()) {
            case APP_APPROVED:
            case APP_FAVOURITED:
                    return new AppHistoryRow(activity, event);

            case USER_COMMENT:
            case USER_MENTIONED:
                return new CommentHistoryRow(activity, event);

            case USER_IN_TOP_HUNTERS:
                return new TopHunterHistoryRow(activity, event);

            case USER_FOLLOWED:
                return new UserProfileHistoryRow(activity, event);

            case COLLECTION_CREATED:
            case COLLECTION_FAVOURITED:
            case COLLECTION_UPDATED:
                return new CollectionHistoryRow(activity, event);

            case APP_REJECTED:
                return new AppRejectHistoryRow(activity, event);
        }

        return new AppRejectHistoryRow(activity, event);
    }
}
