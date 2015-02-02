package com.shtaigaway.apphunt.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.shtaigaway.apphunt.R;

public class SettingsFragment extends BaseFragment {

    private View view;
    private RelativeLayout settingsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        initUI();

        return view;

    }

    private void initUI() {
        settingsLayout = (RelativeLayout) view.findViewById(R.id.settings);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            Animation enterAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_in);
            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Animation notificationEnterAnim = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.slide_in_top_notification);
                    notificationEnterAnim.setFillAfter(true);
                    settingsLayout.startAnimation(notificationEnterAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            return enterAnim;
        } else {
            Animation outAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);;

            settingsLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_out_top));

            return outAnim;
        }
    }
}
