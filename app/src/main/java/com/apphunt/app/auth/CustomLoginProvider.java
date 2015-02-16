package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/13/15.
 */
public class CustomLoginProvider extends BaseLoginProvider implements LoginProvider {
    public static final String PROVIDER_NAME = "custom";

    public CustomLoginProvider(Activity activity) {
        super(activity);
    }

    @Override
    public void onCreate(Activity activity, Bundle savedStateInstance) {

    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

}
