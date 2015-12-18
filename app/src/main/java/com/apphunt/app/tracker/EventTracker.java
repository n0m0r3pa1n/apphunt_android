package com.apphunt.app.tracker;

import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.CallToActionEvent;
import com.apphunt.app.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nmp on 15-12-18.
 */
public class EventTracker {
    private static final int MIN_APP_COMMENTS_COUNT = 2;
    private static final int MIN_VOTES_COUNT = 5;
    private static final int MIN_INSTALL_CLICKS = 4;

    private List<String> trackedEvents = new ArrayList<>();

    public static EventTracker instance;
    public static EventTracker getInstance() {
        if(instance == null) {
            instance = new EventTracker();
            instance.trackedEvents.add(TrackingEvents.UserSentComment);
            instance.trackedEvents.add(TrackingEvents.UserSentReplyComment);
            instance.trackedEvents.add(TrackingEvents.UserVotedApp);
            instance.trackedEvents.add(TrackingEvents.UserOpenedAppInMarket);
        }

        return instance;
    }

    public void trackEvent(String eventName) {
        if(!trackedEvents.contains(eventName)) {
            return;
        }

        boolean shouldCallToAction = false;
        int eventCount = SharedPreferencesHelper.getIntPreference(eventName, 0);
        eventCount++;
        SharedPreferencesHelper.setPreference(eventName, eventCount);

        switch(eventName) {
            case TrackingEvents.UserSentComment:
            case TrackingEvents.UserSentReplyComment:
                if(eventCount >= MIN_APP_COMMENTS_COUNT) {
                    shouldCallToAction = true;
                }
                break;
            case TrackingEvents.UserVotedApp:
                if(eventCount >= MIN_VOTES_COUNT) {
                    shouldCallToAction = true;
                }
                break;
            case TrackingEvents.UserOpenedAppInMarket:
                if(eventCount >= MIN_INSTALL_CLICKS) {
                    shouldCallToAction = true;
                }
                break;
        }

        if(!shouldCallToAction) {
            return;
        }

        BusProvider.getInstance().post(new CallToActionEvent());
        SharedPreferencesHelper.setPreference(Constants.KEY_CALL_TO_ACTION, true);
    }

    public void resetActions() {
        for(String eventName: trackedEvents) {
            SharedPreferencesHelper.setPreference(eventName, 0);
        }
        SharedPreferencesHelper.setPreference(Constants.KEY_CALL_TO_ACTION, false);
    }

}
