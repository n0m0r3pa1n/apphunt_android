package com.apphunt.app.ui.adapters.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.ui.fragments.base.BaseFragment;
import com.apphunt.app.ui.fragments.login.EmailInviteFragment;
import com.apphunt.app.ui.fragments.login.ProviderInviteFragment;
import com.apphunt.app.ui.fragments.login.SMSInviteFragment;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class InviteOptionsAdapter extends FragmentStatePagerAdapter {

    private final String providerName;

    public InviteOptionsAdapter(FragmentManager fm, AppCompatActivity activity) {
        super(fm);

        this.providerName = LoginProviderFactory.get(activity).getName();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = null;

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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return providerName;
            case 1:
                return "Email";
            case 2:
                return "SMS";
            default:
                return "Item";
        }
    }
}
