package com.apphunt.app.ui.adapters.invite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.auth.TwitterLoginProvider;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.invites.EmailInviteFragment;
import com.apphunt.app.ui.fragments.invites.ProviderInviteFragment;
import com.apphunt.app.ui.fragments.invites.SMSInviteFragment;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class InviteOptionsAdapter extends FragmentStatePagerAdapter {

    private final String loginType;
    private AppCompatActivity activity;
    private boolean withProviderInvite = false;

    public InviteOptionsAdapter(FragmentManager fm, AppCompatActivity activity) {
        super(fm);

        this.activity = activity;
        this.loginType = LoginProviderFactory.get(activity).getLoginType();

        if (LoginProviderFactory.get(activity).getName().equals(TwitterLoginProvider.PROVIDER_NAME)) {
            withProviderInvite = true;
        }
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;

        if (!withProviderInvite) {
            position += 1;
        }

        switch (position) {
            case 0:
                fragment = ProviderInviteFragment.newInstance();
                break;
            case 1:
                fragment = EmailInviteFragment.newInstance();
                break;
            case 2:
                fragment = SMSInviteFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        if (LoginProviderFactory.get(activity).getName().equals(TwitterLoginProvider.PROVIDER_NAME)) {
            return 3;
        } else {
            return 2;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (!withProviderInvite) {
            position += 1;
        }

        switch (position) {
            case 0:
                return loginType;
            case 1:
                return "Email";
            case 2:
                return "SMS";
            default:
                return "Item";
        }
    }
}
