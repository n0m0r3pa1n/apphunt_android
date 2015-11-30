package com.apphunt.app.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apphunt.app.R;
import com.apphunt.app.WebviewActivity;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.GetAdApiEvent;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-11-30.
 */
public class AdView extends LinearLayout {

    private LayoutInflater inflater;

    @InjectView(R.id.ad_picture)
    ImageView adPicture;

    public AdView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        View view = getLayoutInflater().inflate(R.layout.layout_ad, this, true);
        ButterKnife.inject(this, view);
        ApiClient.getClient(context).getAd();
    }

    @Subscribe
    public void onAdReceived(final GetAdApiEvent event) {
        if(event == null || event.getAd() == null) {
            adPicture.setVisibility(GONE);
            return;
        }

        Picasso.with(getContext()).load(event.getAd().getPicture()).into(adPicture);
        adPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WebviewActivity.class);
                intent.putExtra(Constants.EXTRA_URL, event.getAd().getLink());
                getContext().startActivity(intent);
            }
        });
    }

}
