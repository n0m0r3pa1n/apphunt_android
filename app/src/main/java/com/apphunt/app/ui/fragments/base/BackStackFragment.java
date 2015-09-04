package com.apphunt.app.ui.fragments.base;

import com.apphunt.app.event_bus.BusProvider;

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
        BusProvider.getInstance().unregister(this);
    }
}
