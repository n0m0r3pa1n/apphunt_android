package com.apphunt.app.db.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nmp on 15-7-31.
 */
public class InstalledApp extends RealmObject {

    @PrimaryKey
    private String packageName;
    private Date dateInstalled;
    private boolean hasNotificationShowed;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getDateInstalled() {
        return dateInstalled;
    }

    public void setDateInstalled(Date age) {
        this.dateInstalled = age;
    }

    public boolean isHasNotificationShowed() {
        return hasNotificationShowed;
    }

    public void setHasNotificationShowed(boolean hasNotificationShowed) {
        this.hasNotificationShowed = hasNotificationShowed;
    }
}
