package com.apphunt.app.ui.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.apphunt.app.ui.interfaces.OnEndReachedListener;

/**
 * Created by nmp on 15-7-13.
 */
public class EndlessRecyclerScrollListener extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager layoutManager;
    private OnEndReachedListener listener;

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            listener.onEndReached();

            loading = true;
        }
    }

    public EndlessRecyclerScrollListener(OnEndReachedListener listener, LinearLayoutManager layoutManager) {
        this.listener = listener;
        this.layoutManager = layoutManager;
    }

    public void setOnEndReachedListener(OnEndReachedListener listener) {
        this.listener = listener;
    }

}
