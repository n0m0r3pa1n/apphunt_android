package com.apphunt.app.smart_rate;


import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.apphunt.app.R;
import com.apphunt.app.smart_rate.fragments.BaseRateFragment;
import com.apphunt.app.smart_rate.fragments.FeedbackFragment;
import com.apphunt.app.smart_rate.fragments.LoveFragment;
import com.apphunt.app.smart_rate.fragments.RateFragment;
import com.apphunt.app.smart_rate.listeners.OnNoClickListener;
import com.apphunt.app.smart_rate.listeners.OnSendClickListener;
import com.apphunt.app.smart_rate.listeners.OnYesClickListener;
import com.apphunt.app.utils.SharedPreferencesHelper;

public class SmartRate {
    private static SmartRate instance;
    private ActionBarActivity activity;
    private String appName;
    private int showRun;
    private String showLocation;
    private long appRun;

    public SmartRate(ActionBarActivity activity, String appSpiceId, String appId) {
        this.activity = activity;
        this.appName = getApplicationName(activity);
        incrementAppRuns();
    }

    public static void init(ActionBarActivity activity, String appSpiceId, String appId) {
        instance = new SmartRate(activity, appSpiceId, appId);
    }

    private void incrementAppRuns() {
        appRun = SharedPreferencesHelper.getLongPreference(activity, SmartRateConstants.SMART_RATE_APP_RUNS_KEY);
        appRun++;
        SharedPreferencesHelper.setPreference(activity, SmartRateConstants.SMART_RATE_APP_RUNS_KEY, appRun);
    }

    private static String getApplicationName(ActionBarActivity activity) {
        return activity.getApplicationInfo().loadLabel(activity.getPackageManager()).toString();
    }

    public static void show(String showLocation) {
        instance.showLoveFragment(showLocation);
    }

    private void showLoveFragment(String showLocation) {
        LoveFragment loveFragment = new LoveFragment();
        loveFragment.setOnYesListener(onLoveYesClickListener);
        loveFragment.setOnNoClickListener(onLoveNoClickListener);

        activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                .add(R.id.container, loveFragment, SmartRateConstants.TAG_LOVE_FRAGMENT)
                .addToBackStack(SmartRateConstants.TAG_LOVE_FRAGMENT)
                .commit();
    }

    private OnYesClickListener onLoveYesClickListener = new OnYesClickListener() {
        @Override
        public void onYesClick(BaseRateFragment fragment, View view) {
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

            RateFragment rateFragment = new RateFragment();
            rateFragment.setOnYesListener(onRateYesClickListener);
            rateFragment.setOnNoClickListener(onRateNoClickListener);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                    .add(R.id.container, rateFragment, SmartRateConstants.TAG_RATE_FRAGMENT)
                    .addToBackStack(SmartRateConstants.TAG_RATE_FRAGMENT)
                    .commit();
        }
    };

    private OnNoClickListener onLoveNoClickListener = new OnNoClickListener() {
        @Override
        public void onNoClick(BaseRateFragment fragment, View view) {
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

            FeedbackFragment feedbackFragment = new FeedbackFragment();
            feedbackFragment.setOnSendClickListener(onSendClickListener);

            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in, R.anim.slide_out_top)
                    .add(R.id.container, feedbackFragment, SmartRateConstants.TAG_FEEDBACK_FRAGMENT)
                    .addToBackStack(SmartRateConstants.TAG_FEEDBACK_FRAGMENT)
                    .commit();
        }
    };

    private OnYesClickListener onRateYesClickListener = new OnYesClickListener() {
        @Override
        public void onYesClick(BaseRateFragment fragment, View view) {
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    };

    private OnNoClickListener onRateNoClickListener = new OnNoClickListener() {
        @Override
        public void onNoClick(BaseRateFragment fragment, View view) {
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };

    private OnSendClickListener onSendClickListener = new OnSendClickListener() {
        @Override
        public void onSendClick(BaseRateFragment fragment, String feedbackMessage) {
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };
}
