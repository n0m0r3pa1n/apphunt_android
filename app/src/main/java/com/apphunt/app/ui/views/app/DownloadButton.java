package com.apphunt.app.ui.views.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.db.models.ClickedApp;
import com.apphunt.app.utils.FlurryWrapper;
import com.apphunt.app.utils.PackagesUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DownloadButton extends LinearLayout {
    private TextView textView;

    private String appPackage;
    private String screen;

    public DownloadButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context, null);
        }
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public void setTrackingScreen(String screen) {
        this.screen = screen;
    }

    private void init(final Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_flat_blue_button, this, true);
        textView = (TextView) view.findViewById(R.id.tv_download);

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.DownloadButton, 0, 0);

        ColorStateList backgroundColor = array.getColorStateList(R.styleable.DownloadButton_backgroundColor);
        ColorStateList textColor = array.getColorStateList(R.styleable.DownloadButton_textColor);
        int textSize = array.getDimensionPixelSize(R.styleable.DownloadButton_buttonTextSize, 1);

        textView.setBackgroundColor(backgroundColor.getDefaultColor());
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(appPackage)) {
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("appPackage", appPackage);
                params.put("screen", screen);

                if (PackagesUtils.isPackageInstalled(appPackage, getContext())) {
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedInstalledApp, params);
                    Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(appPackage);
                    getContext().startActivity(intent);
                } else {
                    FlurryWrapper.logEvent(TrackingEvents.UserOpenedAppInMarket, params);
                    PackagesUtils.openInMarket(getContext(), appPackage);

                    updateOrCreateClickedAppObject();
                }
            }
        });
    }

    private void updateOrCreateClickedAppObject() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).deleteRealmIfMigrationNeeded().build();
        Realm realm = Realm.getInstance(realmConfiguration);
        ClickedApp clickedApp = realm.where(ClickedApp.class).equalTo("packageName", appPackage).findFirst();
        realm.beginTransaction();

        if (clickedApp != null) {
            clickedApp.setDateClicked(new Date());
        } else {
            clickedApp = realm.createObject(ClickedApp.class);
            clickedApp.setPackageName(appPackage);
            clickedApp.setDateClicked(new Date());
        }

        realm.commitTransaction();
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
        if(PackagesUtils.isPackageInstalled(appPackage, getContext())) {
            textView.setText(R.string.open);
        } else {
            textView.setText(R.string.install);
        }

    }
}
