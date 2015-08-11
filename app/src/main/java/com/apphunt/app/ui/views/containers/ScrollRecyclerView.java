package com.apphunt.app.ui.views.containers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.ui.interfaces.OnEndReachedListener;
import com.apphunt.app.ui.listeners.EndlessRecyclerScrollListener;
import com.apphunt.app.utils.SharedPreferencesHelper;
import com.apphunt.app.utils.SoundsUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by nmp on 15-7-13.
 */
public class ScrollRecyclerView extends LinearLayout {
    private View view;
    private LayoutInflater inflater;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int totalItemsCount;
    private OnEndReachedListener listener;

    public ScrollRecyclerView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init();
        }
    }

    public ScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init();
        }
    }

    public ScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init();
        }
    }

    private void init() {
        view = getLayoutInflater().inflate(R.layout.view_scroll_recyclerview, this, true);
        ButterKnife.inject(this, view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
    }

    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    public void setOnEndReachedListener(final OnEndReachedListener listener) {
        this.listener = listener;
    }

    public void showBottomLoader() {
        RelativeLayout bottomLoaderLayout = (RelativeLayout) view.findViewById(R.id.more_loader_layout);
        GifImageView bottomLoader = (GifImageView) view.findViewById(R.id.more_loader);
        boolean soundEnabled = SharedPreferencesHelper.getBooleanPreference(Constants.IS_SOUNDS_ENABLED);
        if (bottomLoaderLayout.getVisibility() != View.VISIBLE) {
            if (soundEnabled)
                SoundsUtils.playSound(getContext(), R.raw.notification_1);

            bottomLoader.setBackgroundResource(R.drawable.loader_white);
            bottomLoaderLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideBottomLoader() {
        RelativeLayout bottomLoaderLayout = (RelativeLayout) view.findViewById(R.id.more_loader_layout);
        if (bottomLoaderLayout != null && bottomLoaderLayout.getVisibility() == View.VISIBLE) {
            bottomLoaderLayout.setVisibility(View.GONE);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter, int totalItemsCount) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        this.totalItemsCount = totalItemsCount;

        recyclerView.addOnScrollListener(new EndlessRecyclerScrollListener(new OnEndReachedListener() {
            @Override
            public void onEndReached() {
                if (ScrollRecyclerView.this.adapter.getItemCount() >= ScrollRecyclerView.this.totalItemsCount) {
                    return;
                }

                showBottomLoader();
                ScrollRecyclerView.this.listener.onEndReached();
            }
        }, layoutManager));
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.Adapter getAdapter() {
        return recyclerView.getAdapter();
    }

    public void resetAdapter() {
        adapter = null;
        recyclerView.removeAllViews();
        recyclerView.setAdapter(null);
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }
}
