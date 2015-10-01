package com.apphunt.app.ui.models.drawer;

/**
 * Created by nmp on 15-6-9.
 */
public class DrawerLabel extends DrawerItem {
    private String label;
    public DrawerLabel(String label) {
        super(Type.LABEL);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public DrawerLabel setLabel(String label) {
        this.label = label;
        return this;
    }
}
