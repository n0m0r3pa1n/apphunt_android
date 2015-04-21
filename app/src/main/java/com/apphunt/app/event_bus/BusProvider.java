package com.apphunt.app.event_bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/21/15.
 */
public class BusProvider {
    private static Bus instance;

    public static Bus getInstance() {
        if (instance == null) {
            instance = new Bus(ThreadEnforcer.ANY);
        }

        return instance;
    }
}
