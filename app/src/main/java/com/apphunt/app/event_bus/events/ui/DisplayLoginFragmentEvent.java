package com.apphunt.app.event_bus.events.ui;

/**
 * Created by nmp on 15-10-25.
 */
public class DisplayLoginFragmentEvent {
    private int messageResId;
    private String message;
    private boolean canBeSkipped;

    public DisplayLoginFragmentEvent(String message, boolean canBeSkipped) {
        this.message = message;
        this.canBeSkipped = canBeSkipped;
    }

    public DisplayLoginFragmentEvent(int messageResId, boolean canBeSkipped) {
        this.messageResId = messageResId;
        this.canBeSkipped = canBeSkipped;
    }

    public String getMessage() {
        return message;
    }

    public boolean canBeSkipped() {
        return canBeSkipped;
    }

    public int getMessageResId() {
        return messageResId;
    }
}
