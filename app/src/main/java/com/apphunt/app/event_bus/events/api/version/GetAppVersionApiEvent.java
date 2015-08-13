package com.apphunt.app.event_bus.events.api.version;

import com.apphunt.app.api.apphunt.models.version.Version;

public class GetAppVersionApiEvent {
    private Version version;

    public GetAppVersionApiEvent(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }
}
