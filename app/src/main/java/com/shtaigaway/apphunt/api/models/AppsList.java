package com.shtaigaway.apphunt.api.models;


import java.util.ArrayList;

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

    public void setDate(String date) {
        this.date = date;
    }

    public boolean haveMoreApps() {
        return (page <= totalPages);
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
