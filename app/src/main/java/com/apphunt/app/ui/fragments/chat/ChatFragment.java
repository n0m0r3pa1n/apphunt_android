package com.apphunt.app.ui.fragments.chat;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphunt.app.R;
import com.apphunt.app.api.apphunt.clients.sockets.SocketConnectionManager;
import com.apphunt.app.api.apphunt.models.chat.ChatMessage;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;
import com.apphunt.app.auth.LoginProviderFactory;
import com.apphunt.app.ui.fragments.base.BaseFragment;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by nmp on 16-1-27.
 */
public class ChatFragment extends BaseFragment implements SocketConnectionManager.OnChatListener {
    private Handler handler = new Handler();
    public ChatFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SocketConnectionManager.getInstance().addChatListener(this);
        SocketConnectionManager.getInstance().emitAddUserToTopHuntersChat(LoginProviderFactory.get(getActivity()).getUser());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SocketConnectionManager.getInstance().removeChatListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               SocketConnectionManager.getInstance().emitNewMessage("TTTTTTTT", LoginProviderFactory.get(getActivity()).getUser().getId());
            }
        }, 2000);
        return view;
    }

    @Override
    public void onNewMessage(ChatMessage message) {

    }

    @Override
    public void onUsersList(List<ChatUser> users) {

    }
}
