package com.apphunt.app.ui.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private int title;
    private String previousTitle;

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getPreviousTitle() {
        return previousTitle;
    }

    public void setPreviousTitle(String previousTitle) {
        this.previousTitle = previousTitle;
    }
}
