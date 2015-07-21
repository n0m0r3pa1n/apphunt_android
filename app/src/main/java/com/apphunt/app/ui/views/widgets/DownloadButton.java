package com.apphunt.app.ui.views.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.utils.InstalledPackagesUtils;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

public class DownloadButton extends LinearLayout {
    private TextView textView;

    private String appPackage;
    public DownloadButton(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context) {
        textView = new TextView(context);
        textView.setBackgroundColor(Color.parseColor("#4CAF50"));
        final int padding = 4;
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(padding, padding, padding, padding);
        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(getResources().getDimension(R.dimen.details_download_text_size));

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(appPackage)) {
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("appPackage", appPackage);

                if (InstalledPackagesUtils.isPackageInstalled(appPackage, getContext())) {
                    FlurryAgent.logEvent(TrackingEvents.UserOpenedInstalledApp, params);
                    Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(appPackage);
                    getContext().startActivity(intent);
                } else {
                    FlurryAgent.logEvent(TrackingEvents.UserOpenedAppInMarket, params);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appPackage));
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        marketIntent.setData(Uri.parse("market://details?id=" + appPackage));
                        getContext().startActivity(marketIntent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        marketIntent.setData(Uri.parse(appPackage));
                        getContext().startActivity(marketIntent);
                    }
                }
            }
        });
        this.addView(textView);
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
        if(InstalledPackagesUtils.isPackageInstalled(appPackage, getContext())) {
            textView.setText(R.string.open);
        } else {
            textView.setText(R.string.download);
        }

    }
}
