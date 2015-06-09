package com.apphunt.app.ui.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private int title;
    private String previousTitle;
    private String fragmentTag;
    private boolean consumedBack = false;

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

    public String getFragmentTag() {
        return fragmentTag;
    }

    public void setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    public boolean isConsumedBack() {
        return consumedBack;
    }

    public void setIsConsumedBack(boolean consumedBack) {
        this.consumedBack = consumedBack;
    }
}
