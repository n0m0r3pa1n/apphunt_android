package com.apphunt.app.api.apphunt.models.comments;

public class NewComment {

    private String userId;
    private String text;
    private String parentId;
    private String appId;
    private String mentionedUserId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setMentionedUserId(String mentionedUserId) {
        this.mentionedUserId = mentionedUserId;
    }
}
