package com.apphunt.app.ui.fragments.base;

import com.apphunt.app.event_bus.BusProvider;
import com.crashlytics.android.Crashlytics;

/**
 * Created by nmp on 15-8-14.
 */
public class BackStackFragment extends BaseFragment {

    private boolean isRegistered = false;

    public void registerForEvents() {
        if(!isRegistered) {
            isRegistered = true;
            BusProvider.getInstance().register(this);
        }
    }

    public void unregisterForEvents() {
        if(isRegistered) {
            isRegistered = false;
            BusProvider.getInstance().unregister(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            BusProvider.getInstance().unregister(this);
        } catch(Exception e) {
            Crashlytics.logException(e);
        }
    }
}
