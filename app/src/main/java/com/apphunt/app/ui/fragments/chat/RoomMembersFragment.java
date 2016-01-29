package com.apphunt.app.ui.fragments.chat;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.sockets.SocketConnectionManager;
import com.apphunt.app.api.apphunt.models.chat.ChatMessage;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;
import com.apphunt.app.ui.adapters.chat.UsersListAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RoomMembersFragment extends Fragment implements SocketConnectionManager.OnChatListener {

    private Activity activity;

    @InjectView(R.id.tv_users_count22)
    TextView tvUsersCount;

    @InjectView(R.id.rv_users)
    RecyclerView rvUsers;

    public RoomMembersFragment() {
        // Required empty public constructor
    }

    public static RoomMembersFragment newInstance() {
        return new RoomMembersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SocketConnectionManager.getInstance().removeChatListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_members, container, false);
        ButterKnife.inject(this, view);

        rvUsers.setItemAnimator(new DefaultItemAnimator());
        rvUsers.setLayoutManager(new LinearLayoutManager(activity));
        rvUsers.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SocketConnectionManager.getInstance().addChatListener(this);
    }

    @Override
    public void onNewMessage(ChatMessage message) {

    }

    @Override
    public void onUsersList(final List<ChatUser> users) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvUsersCount.setText(getResources().getQuantityString(R.plurals.user, users.size(), users.size()));
                rvUsers.setAdapter(new UsersListAdapter(users));
            }
        });
    }
}