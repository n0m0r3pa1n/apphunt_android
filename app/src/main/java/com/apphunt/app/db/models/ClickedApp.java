package com.apphunt.app.db.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nmp on 15-7-31.
 */
public class ClickedApp extends RealmObject{
    @PrimaryKey
    private String packageName;
    private Date dateClicked;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getDateClicked() {
        return dateClicked;
    }

    public void setDateClicked(Date age) {
        this.dateClicked = age;
    }
}
