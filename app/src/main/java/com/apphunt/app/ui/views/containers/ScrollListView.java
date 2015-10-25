package com.apphunt.app.ui.views.containers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.apphunt.app.R;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessScrollListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-6-30.
 */
public class ScrollListView extends LinearLayout {

    private int totalItemsCount;

    private OnEndReachedListener listener;

    private View view;
    @InjectView(R.id.listview)
    ListView listView;
    private ListAdapter adapter;
    private LayoutInflater inflater;

    public ScrollListView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init();
        }
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init();
        }
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init();
        }
    }

    private void init() {
        view = getLayoutInflater().inflate(R.layout.view_scroll_listview, this, true);
        ButterKnife.inject(this, view);
        listView.setOnScrollListener(new EndlessScrollListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                if (adapter.getCount() >= totalItemsCount) {
                    return;
                }

                listener.onEndReached();
            }
        }));
    }

    public void setAdapter(ListAdapter adapter, int totalItemsCount) {
        this.adapter = adapter;
        listView.setAdapter(adapter);
        this.totalItemsCount = totalItemsCount;
    }

    public ListView getListView() {
        return  listView;
    }

    public void setOnItemClickListener(AbsListView.OnItemClickListener onItemClickListener) {
        listView.setOnItemClickListener(onItemClickListener);
    }

    public void resetListView() {
        listView.setAdapter(null);
    }

    public void smoothScrollToPosition(int position) {
        listView.smoothScrollToPosition(position);
    }

    public void setOnEndReachedListener(OnEndReachedListener listener) {
        this.listener = listener;
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }
}
