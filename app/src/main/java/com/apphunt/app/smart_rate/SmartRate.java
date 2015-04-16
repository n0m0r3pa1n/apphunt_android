package com.apphunt.app.smart_rate;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.apphunt.app.smart_rate.variables.RateDialogVariable;
import com.apphunt.app.utils.Constants;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.appspice.android.AppSpice;

public class SmartRate {
    private static SmartRate instance;
    private final SharedPreferences preferences;
    private static RateDialogVariable rateDialogVariable;
    private ActionBarActivity activity;
    private static long appRun;

    private SmartRate(ActionBarActivity activity, String appId) {
        this.activity = activity;
        this.preferences = activity.getSharedPreferences(SmartRateConstants.SMART_RATE_PREFERENCES, Context.MODE_PRIVATE);
        incrementAppRuns();
        rateDialogVariable = new RateDialogVariable();
        rateDialogVariable.appRun = preferences.getLong(SmartRateConstants.SMART_RATE_VARIABLE_APP_RUN_KEY, 0);
        rateDialogVariable.showLocation = preferences.getString(SmartRateConstants.SMART_RATE_VARIABLE_SHOW_LOCATION_KEY, "");
        AppSpice.init(activity, appId);
        if (rateDialogVariable.isUndefined()) {
            AppSpice.requestVariable(SmartRateConstants.SMART_RATE_DIALOG_VARIABLE, RateDialogVariable.class);
        }
    }

    public static void init(ActionBarActivity activity, String appId) {
        instance = new SmartRate(activity, appId);
    }

    public static void setRateDialogVariable(RateDialogVariable rateDialogVariable) {
        SmartRate.rateDialogVariable = rateDialogVariable;
        SmartRate.instance.saveRateDialogVariable();
    }

    private void incrementAppRuns() {
        appRun = preferences.getLong(SmartRateConstants.SMART_RATE_APP_RUNS_KEY, 0);
        appRun++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SmartRateConstants.SMART_RATE_APP_RUNS_KEY, appRun);
        editor.apply();
    }

    public static void show(String showLocation) {
        if (appRun == rateDialogVariable.appRun && showLocation.equals(rateDialogVariable.showLocation)) {
            instance.showLoveFragment();
        }
    }

    private void showLoveFragment() {
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
            FlurryAgent.logEvent("love.yes.click");
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
            FlurryAgent.logEvent("love.no.click");
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
            FlurryAgent.logEvent("rate.yes.click");
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    };

    private OnNoClickListener onRateNoClickListener = new OnNoClickListener() {
        @Override
        public void onNoClick(BaseRateFragment fragment, View view) {
            FlurryAgent.logEvent("rate.no.click");
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };

    private OnSendClickListener onSendClickListener = new OnSendClickListener() {
        @Override
        public void onSendClick(BaseRateFragment fragment, String feedbackMessage) {
            Map<String, String> data = new HashMap<>();
            data.put("message", feedbackMessage);
            FlurryAgent.logEvent("feedback.send.click", data);
            activity.getSupportFragmentManager().popBackStack(fragment.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    };

    private void saveRateDialogVariable() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SmartRateConstants.SMART_RATE_VARIABLE_APP_RUN_KEY, rateDialogVariable.appRun);
        editor.putString(SmartRateConstants.SMART_RATE_VARIABLE_SHOW_LOCATION_KEY, rateDialogVariable.showLocation);
        editor.apply();
    }

    public static void onError() {
        if (rateDialogVariable.isUndefined()) {
            Random random = new Random();
            rateDialogVariable.appRun = random.nextInt(8) + 3;
            rateDialogVariable.showLocation = Constants.SMART_RATE_LOCATION_APP_SAVED;
            instance.saveRateDialogVariable();
        }
    }
}
