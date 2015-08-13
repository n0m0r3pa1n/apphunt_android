package com.apphunt.app.ui.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.apphunt.app.event_bus.BusProvider;

public class BaseFragment extends Fragment {

    private int title;
    private boolean isRegistered = false;
    private String previousTitle;
    private String fragmentTag;
    public String getStringTitle() {
        return "";
    };

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

    public void registerForEvents() {
        if(!isRegistered) {
            isRegistered = true;
            BusProvider.getInstance().register(this);
        }
    }

    public void unregisterForEvents() {
        if(isRegistered) {
            isRegistered = false;
            BusProvider.getInstance().unregister(this);
        }
    }

    protected void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
