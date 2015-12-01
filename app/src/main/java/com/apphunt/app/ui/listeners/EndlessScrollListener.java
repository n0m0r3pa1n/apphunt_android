package com.apphunt.app.ui.listeners;

import android.widget.AbsListView;

import com.apphunt.app.ui.interfaces.OnEndReachedListener;

/**
 * Created by nmp on 15-6-30.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 2;
    private int previousTotal = 0;
    private boolean loading = true;
    private OnEndReachedListener listener;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            if (listener != null) {
                listener.onEndReached();
            }
            loading = true;
        }
    }

    public EndlessScrollListener() {
    }

    public void resetPreviousTotal() {
        previousTotal = 0;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(OnEndReachedListener listener) {
        this.listener = listener;
    }

    public void setOnEndReachedListener(OnEndReachedListener listener) {
        this.listener = listener;
    }
}
