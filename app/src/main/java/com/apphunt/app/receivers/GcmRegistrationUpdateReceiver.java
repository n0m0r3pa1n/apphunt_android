package com.apphunt.app.receivers;

import android.content.Context;
import android.content.Intent;

import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.FlurryWrapper;

import kr.nectarine.android.fruitygcm.receiver.FruityRegistrationIdUpdateReceiver;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/20/15.
 */
public class GcmRegistrationUpdateReceiver extends FruityRegistrationIdUpdateReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        super.onReceive(context, intent);
    }

    @Override
    public void onRegistrationIdRenewed(String regId) {
        FlurryWrapper.init(context);
        FlurryWrapper.logEvent(TrackingEvents.UserReceivedRegistrationIdRenewal);
    }

    @Override
    public void onRegistrationIdRenewFailed() {

    }
}