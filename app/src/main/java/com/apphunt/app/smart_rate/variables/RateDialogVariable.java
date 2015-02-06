package com.apphunt.app.smart_rate.variables;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public class RateDialogVariable {
    public long appRun = 0;
    public String showLocation = "";

    public boolean isUndefined() {
        return appRun == 0 || showLocation.isEmpty();
    }
}
