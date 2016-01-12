package com.apphunt.app.ui.views.ads;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apphunt.app.R;
import com.apphunt.app.WebviewActivity;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.ads.Ad;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.GetAdApiEvent;
import com.apphunt.app.utils.FlurryWrapper;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by nmp on 15-11-30.
 */
public class AppHuntAdView extends LinearLayout {

    private LayoutInflater inflater;

    @InjectView(R.id.ad_picture)
    ImageView adPicture;

    @InjectView(R.id.loading)
    CircularProgressBar circularProgressBar;

    public AppHuntAdView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AppHuntAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AppHuntAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppHuntAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(context);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            BusProvider.getInstance().register(this);
        } catch(Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            BusProvider.getInstance().unregister(this);
        } catch(Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    protected LayoutInflater getLayoutInflater() {
        if(inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;
    }

    private void init(Context context) {
        View view = getLayoutInflater().inflate(R.layout.layout_apphunt_ad, this, true);
        ButterKnife.inject(this, view);
        ApiClient.getClient(context).getAd();
    }

    @Subscribe
    public void onAdReceived(final GetAdApiEvent event) {
        if(event == null || event.getAd() == null) {
            setVisibility(GONE);
            circularProgressBar.setVisibility(GONE);
            adPicture.setVisibility(GONE);
            return;
        }

        final Ad ad = event.getAd();
        setVisibility(VISIBLE);
        circularProgressBar.setVisibility(GONE);
        adPicture.setVisibility(VISIBLE);
        Picasso.with(getContext()).load(ad.getPicture()).into(adPicture);
        adPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = ad.getLink();
                FlurryWrapper.logEvent(TrackingEvents.UserOpenedAdd, new HashMap<String, String>() {{
                    put("url", url);
                }});
                if (url.contains("play.google.com/store/apps/details?id=")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return;
                    } catch (ActivityNotFoundException e) {
                        Crashlytics.logException(e);
                    }
                }

                Intent intent = new Intent(getContext(), WebviewActivity.class);
                intent.putExtra(Constants.EXTRA_URL, url);
                getContext().startActivity(intent);
            }
        });
    }

}
