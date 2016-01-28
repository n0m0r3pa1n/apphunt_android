package com.apphunt.app.ui.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.ui.fragments.base.BaseFragment;

import butterknife.ButterKnife;

public class RoomMembersFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RoomMembersFragment() {
        // Required empty public constructor
    }

    public static RoomMembersFragment newInstance() {
        return new RoomMembersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_members, container, false);
        ButterKnife.inject(this, view);

        return inflater.inflate(R.layout.fragment_room_members, container, false);
    }
}
