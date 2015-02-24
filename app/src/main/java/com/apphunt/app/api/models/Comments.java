package com.apphunt.app.api.models;

import java.util.ArrayList;

public class Comments {
    private ArrayList<Comment> comments = new ArrayList<>();
    private int totalCount;
    private int page;
    private int totalPages;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
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

    @Override
    public String toString() {
        return "Comments{" +
                "comments=" + comments +
                ", totalCount=" + totalCount +
                ", page=" + page +
                ", totalPages=" + totalPages +
                '}';
    }
}
