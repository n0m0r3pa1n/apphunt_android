package com.apphunt.app.ui.views.gallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.apphunt.app.R;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-7-21.
 */
public class GalleryView extends LinearLayout {
    CircularProgressBar loading;
    View view;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    public GalleryView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GalleryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_gallery, this, false);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        loading = (CircularProgressBar) view.findViewById(R.id.loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        addView(view);
    }

    public void setImages(ArrayList<String> images) {
        loading.setVisibility(GONE);
        recyclerView.setVisibility(VISIBLE);
        adapter = new ImageAdapter(getContext(), images);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
