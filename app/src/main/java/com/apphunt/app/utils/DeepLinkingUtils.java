package com.apphunt.app.utils;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import io.branch.referral.BranchShortLinkBuilder;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/31/15.
 * *
 * * NaughtySpirit 2015
 */
public class DeepLinkingUtils {
    private static final String TAG = DeepLinkingUtils.class.getSimpleName();

    private static DeepLinkingUtils instance;
    private final AppCompatActivity activity;

    public static class DeepLinkingParam {
        private String key;
        private String value;

        public DeepLinkingParam(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static DeepLinkingUtils getInstance(AppCompatActivity activity) {
        if (instance == null) {
            return new DeepLinkingUtils(activity);
        }

        return instance;
    }

    private DeepLinkingUtils(AppCompatActivity activity) {
        this.activity = activity;
    }

    public String generateShortUrl(ArrayList<DeepLinkingParam> params) {
        BranchShortLinkBuilder shortLinkBuilder = new BranchShortLinkBuilder(activity);

        for (DeepLinkingParam param : params) {
            shortLinkBuilder.addParameters(param.key, param.value);
        }

        return shortLinkBuilder.getShortUrl();
    }
}
