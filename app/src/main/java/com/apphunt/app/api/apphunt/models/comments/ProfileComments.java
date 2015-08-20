package com.apphunt.app.api.apphunt.models.comments;

import java.util.ArrayList;

public class ProfileComments {
    private ArrayList<ProfileComment> comments = new ArrayList<>();
    private int totalCount;
    private int page;
    private int totalPages;

    public ArrayList<ProfileComment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<ProfileComment> comments) {
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
