package com.shtaigaway.apphunt.ui.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private int title;

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }
}
