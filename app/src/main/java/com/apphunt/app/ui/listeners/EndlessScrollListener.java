package com.apphunt.app.ui.listeners;

import android.widget.AbsListView;

import com.apphunt.app.ui.interfaces.OnEndReachedListener;

/**
 * Created by nmp on 15-6-30.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {
    private boolean isEndOfList = false;
    private OnEndReachedListener listener;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (isEndOfList && listener != null) {
                listener.onEndReached();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isEndOfList = (firstVisibleItem + visibleItemCount) == totalItemCount;
    }

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(OnEndReachedListener listener) {
        this.listener = listener;
    }

    public void setOnEndReachedListener(OnEndReachedListener listener) {
        this.listener = listener;
    }


}
