package com.apphunt.app.api.apphunt.models.collections.hunters;

import java.util.ArrayList;
import java.util.List;

public class HuntersCollections {
    private List<HuntersCollection> collections = new ArrayList<>();

    public HuntersCollections(List<HuntersCollection> collections) {
        this.collections = collections;
    }

    public List<HuntersCollection> getCollections() {
        return collections;
    }
}
