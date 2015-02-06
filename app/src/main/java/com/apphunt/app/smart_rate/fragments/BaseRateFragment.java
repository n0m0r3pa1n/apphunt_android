package com.apphunt.app.smart_rate.fragments;

import com.apphunt.app.smart_rate.listeners.OnNoClickListener;
import com.apphunt.app.smart_rate.listeners.OnSendClickListener;
import com.apphunt.app.smart_rate.listeners.OnYesClickListener;
import com.apphunt.app.ui.fragments.BaseFragment;

public class BaseRateFragment extends BaseFragment {

    private String fragmentTag;
    private OnYesClickListener onYesClickListener;
    private OnNoClickListener onNoClickListener;
    private OnSendClickListener onSendClickListener;

    public void setOnYesListener(OnYesClickListener onYesListener) {
        this.onYesClickListener = onYesListener;
    }

    public void setOnNoClickListener(OnNoClickListener onNoClickListener) {
        this.onNoClickListener = onNoClickListener;
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    public OnYesClickListener getOnYesClickListener() {
        return onYesClickListener;
    }

    public OnNoClickListener getOnNoClickListener() {
        return onNoClickListener;
    }

    public OnSendClickListener getOnSendClickListener() {
        return onSendClickListener;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    public void setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }
}
