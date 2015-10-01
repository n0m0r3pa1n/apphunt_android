package com.apphunt.app.api.apphunt.models.users;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/28/15.
 * *
 * * NaughtySpirit 2015
 */
public class NamesList {
    private ArrayList<String> names = new ArrayList<>();

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public void addName(String name) {
        this.names.add(name);
    }
}
