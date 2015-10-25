package com.apphunt.app.utils;

import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.ui.DisplayLoginFragmentEvent;

public class LoginUtils {

    public static void showLoginFragment(boolean canBeSkipped) {
        BusProvider.getInstance().post(new DisplayLoginFragmentEvent(null, canBeSkipped));
    }

    public static void showLoginFragment(boolean canBeSkipped, int messageRes) {
        BusProvider.getInstance().post(new DisplayLoginFragmentEvent(messageRes, canBeSkipped));
    }
}
