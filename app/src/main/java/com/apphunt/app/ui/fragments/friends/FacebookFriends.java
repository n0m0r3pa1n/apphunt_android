package com.apphunt.app.ui.fragments.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.base.BaseFragment;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 9/17/15.
 * *
 * * NaughtySpirit 2015
 */
public class FacebookFriends extends BaseFragment {

    private static final String TAG = FacebookFriends.class.getSimpleName();

    public static FacebookFriends newInstance() {
        return new FacebookFriends();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_facebook, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        Log.e(TAG, "Facebook");
    }
}
