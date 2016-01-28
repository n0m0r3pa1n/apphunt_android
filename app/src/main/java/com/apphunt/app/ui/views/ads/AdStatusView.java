package com.apphunt.app.ui.views.ads;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.rest.ApiClient;
import com.apphunt.app.api.apphunt.models.ads.UserAdStatus;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.event_bus.BusProvider;
import com.apphunt.app.event_bus.events.api.ads.UserAdStatusApiEvent;
import com.apphunt.app.event_bus.events.ui.ads.DisplayAdStatusDialogEvent;
import com.apphunt.app.event_bus.events.ui.ads.UpdateAdStatusEvent;
import com.squareup.otto.Subscribe;

/**
 * Created by nmp on 16-1-20.
 */
public class AdStatusView extends ImageButton {

    private Context context;
    private UserAdStatus userAdStatus;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AdStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AdStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    public AdStatusView(Context context) {
        super(context);
        if(!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        this.context = context;
        this.setBackgroundResource(android.R.color.transparent);
        ApiClient.getClient(context).getUserAdStatus(LoginProviderFactory.get(context).getUser().getId());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new DisplayAdStatusDialogEvent(userAdStatus));
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onAdStatusReceived(UserAdStatusApiEvent event) {
        userAdStatus = event.getUserAdStatus();
        if(event.getUserAdStatus().shouldShowAd()) {
            this.setBackgroundResource(R.drawable.ic_adb_off);
        } else {
            this.setBackgroundResource(R.drawable.ic_adb_on);
        }
    }

    @Subscribe
    public void onUpdateAdStatus(UpdateAdStatusEvent event) {
        ApiClient.getClient(context).getUserAdStatus(LoginProviderFactory.get(context).getUser().getId());
    }
}
