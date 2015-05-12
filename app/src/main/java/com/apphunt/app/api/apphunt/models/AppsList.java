package com.apphunt.app.api.apphunt.models;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AppsList {
    private ArrayList<App> apps = new ArrayList<>();
    private int totalCount;
    private int page;
    private int totalPages;
    private String date;

    public ArrayList<App> getApps() {
        return apps;
    }

    public void setApps(ArrayList<App> apps) {
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
}
