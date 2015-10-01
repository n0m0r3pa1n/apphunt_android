package com.apphunt.app.ui.models.history;

import com.google.gson.annotations.SerializedName;

public enum HistoryEventType {
    @SerializedName("appApproved")
    APP_APPROVED("appApproved"),

    @SerializedName("appRejected")
    APP_REJECTED("appRejected"),

    @SerializedName("appFavourited")
    APP_FAVOURITED("appFavourited"),

    @SerializedName("userMentioned")
    USER_MENTIONED("userMentioned"),

    @SerializedName("userFollowed")
    USER_FOLLOWED("userFollowed"),

    @SerializedName("userComment")
    USER_COMMENT("userComment"),

    @SerializedName("userInTopHunters")
    USER_IN_TOP_HUNTERS("userInTopHunters"),

    @SerializedName("collectionCreated")
    COLLECTION_CREATED("collectionCreated"),

    @SerializedName("collectionFavourited")
    COLLECTION_FAVOURITED("collectionFavourited"),

    @SerializedName("collectionUpdated")
    COLLECTION_UPDATED("collectionUpdated");

    private final String text;

    private HistoryEventType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
