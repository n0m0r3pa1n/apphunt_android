package com.apphunt.app.ui.views.ads;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.event_bus.BusProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-11-30.
 */
public class PaidAdView extends LinearLayout {
    public static final String TAG = PaidAdView.class.getSimpleName();

    private LayoutInflater inflater;

    @InjectView(R.id.adView)
    AdView adView;
    private boolean isAdLoaded = false;

    public PaidAdView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public PaidAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public PaidAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaidAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(context);
        }
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    private void init(Context context) {
        View view = getLayoutInflater().inflate(R.layout.layout_paid_ad, this, true);
        ButterKnife.inject(this, view);
    }

    public void loadAd() {
        if(isAdLoaded) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("028F5B0702B89166E8DA7A28A19F31B6").build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                isAdLoaded = true;
                Log.d(TAG, "onAdLoaded: ");
            }
        });
        adView.loadAd(adRequest);
    }
}
