package com.apphunt.app.api.apphunt.models;

public class Pagination {
    private int page;
    private int pageSize;

    public Pagination(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageString() {
        return "page=" + page;
    }

    public String getPageSizeString() {
        return "pageSize=" + pageSize;
    }

    public String getPaginationString() {
        return "page=" + page + "&pageSize=" + pageSize;
    }
}
