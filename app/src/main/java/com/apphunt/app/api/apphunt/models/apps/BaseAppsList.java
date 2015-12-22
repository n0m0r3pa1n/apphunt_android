package com.apphunt.app.api.apphunt.models.apps;


import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BaseAppsList {
    @SerializedName("apps")
    private ArrayList<BaseApp> apps = new ArrayList<>();
    private int totalCount;
    private int page;
    private int totalPages;
    private String date;

    public ArrayList<BaseApp> getApps() {
        return apps;
    }

    public void setApps(ArrayList<BaseApp> apps) {
        this.apps = apps;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getDate() {
        return date;
    }

    public Calendar getDateAsCalendar() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            calendar.setTime(format.parse(getDate()));
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean haveMoreApps() {
        if (page < totalPages) {
            return true;
        } else if (page == totalPages || page > totalPages) {
            return false;
        }

        return false;
    }

    @Override
    public String toString() {
        return "AppsList{" +
                "apps=" + apps +
                ", totalCount=" + totalCount +
                ", page=" + page +
                ", totalPages=" + totalPages +
                ", date='" + date + '\'' +
                '}';
    }
}
