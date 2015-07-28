package com.apphunt.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.apphunt.app.R;

/**
 * Created by nmp on 15-7-28.
 */
public class CommentsFragment extends BaseFragment {
    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
        }
    }

    @Override
    public int getTitle() {
        return R.string.title_comments;
    }
}
