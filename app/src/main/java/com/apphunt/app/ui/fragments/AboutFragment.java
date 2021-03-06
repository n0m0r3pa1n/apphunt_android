package com.apphunt.app.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.WebviewActivity;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.constants.TrackingEvents;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.utils.FlurryWrapper;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nmp on 15-12-1.
 */
public class AboutFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        FlurryWrapper.logEvent(TrackingEvents.UserViewedAbout);
        return view;
    }

    @Override
    public int getTitle() {
        return R.string.about_title;
    }

    @OnClick(R.id.google_plus_container)
    public void onClickGooglePlus() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "google-plus");
        }});
        Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/105538441962130139197"));
        startActivity(share);
    }

    @OnClick(R.id.twitter_container)
    public void onClickTwitter() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "twitter");
        }});
        Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/TheAppHunt"));
        startActivity(share);
    }

    @OnClick(R.id.facebook_container)
    public void onClickFacebook() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "facebook");
        }});
        Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/theapphunt"));
        startActivity(share);
    }

    @OnClick(R.id.you_tube_container)
    public void onClickYoutube() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "youtube");
        }});
        Intent share = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCPnLaWyjNC6feTXUuhggJ-w"));
        startActivity(share);
    }

    @OnClick(R.id.blog_container)
    public void onClickBlog() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "blog");
        }});
        Intent share = new Intent(getActivity(), WebviewActivity.class);
        share.putExtra(Constants.EXTRA_URL, "http://blog.theapphunt.com");
        startActivity(share);
    }

    @OnClick(R.id.website_container)
    public void onClickWebsite() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "website");
        }});
        Intent share = new Intent(getActivity(), WebviewActivity.class);
        share.putExtra(Constants.EXTRA_URL, "http://www.theapphunt.com");
        startActivity(share);
    }

    @OnClick(R.id.mail_container)
    public void onClickEmail() {
        FlurryWrapper.logEvent(TrackingEvents.UserClickedAboutSocialLink, new HashMap<String, String>(){{
            put("link", "email");
        }});
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "support@theapphunt.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hello AppHunt! :)");

        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
