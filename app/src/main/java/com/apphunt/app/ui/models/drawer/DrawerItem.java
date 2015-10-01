package com.apphunt.app.ui.models.drawer;

public class DrawerItem {

    public static enum Type {HEADER, MENU, DIVIDER, SUBMENU, LABEL}

    private final Type type;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
