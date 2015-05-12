package com.apphunt.app.event_bus.events.api;

import com.apphunt.app.api.apphunt.models.Packages;

public class PackagesFilteredApiEvent {
    private Packages packages;

    public PackagesFilteredApiEvent(Packages packages) {
        this.packages = packages;
    }

    public Packages getPackages() {
        return packages;
    }

    public void setPackages(Packages packages) {
        this.packages = packages;
    }
}
