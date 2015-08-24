package com.apphunt.app.ui.fragments.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 8/24/15.
 * *
 * * NaughtySpirit 2015
 */
public class SMSInviteFragment extends BaseFragment {

    public static SMSInviteFragment newInstance() {
        SMSInviteFragment fragment = new SMSInviteFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_sms, container, false);
        ButterKnife.inject(this, view);

        return view;
    }
}