package com.apphunt.app.api.apphunt.clients.sockets;

import android.util.Log;

import com.apphunt.app.api.apphunt.models.chat.ChatMessage;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.constants.Constants;
import com.crashlytics.android.Crashlytics;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.List;

public class SocketConnectionManager {
    public static final String TAG = SocketConnectionManager.class.getSimpleName();

    private static SocketConnectionManager socketConnectionManager;

    private Socket socket;
    {
        try {
            socket = IO.socket(Constants.BASE_SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.d(TAG, "instance initializer " + e);
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private HistoryConnectionManager historyConnectionManager = new HistoryConnectionManager(socket);
    private ChatConnectionManager chatConnectionManager = new ChatConnectionManager(socket);

    private SocketConnectionManager() {}

    public static SocketConnectionManager getInstance() {
        if(socketConnectionManager == null) {
            socketConnectionManager = new SocketConnectionManager();
        }

        return socketConnectionManager;
    }

    public void emitAddUserToHistory(String userId) {
        historyConnectionManager.emitAddUserToHistory(userId);
    }

    public void emitLastSeenEventId(String userId, String eventId, String date) {
        historyConnectionManager.emitLastSeenEventId(userId, eventId, date);
    }

    public void addRefreshListener(OnRefreshListener listener) {
        historyConnectionManager.addRefreshListener(listener);
    }

    public void removeRefreshListener(OnRefreshListener listener) {
        historyConnectionManager.removeRefreshListener(listener);
    }

    public interface OnRefreshListener {
        void onRefresh(HistoryEvent event);
        void onUnseenEvents(List<String> ids);
    }

    public void emitAddUserToTopHuntersChat(User user) {
        chatConnectionManager.emitAddUserToTopHuntersChat(user);
    }

    public void addChatListener(OnChatListener listener) {
        chatConnectionManager.addChatListener(listener);
    }

    public void removeChatListener(OnChatListener listener) {
        chatConnectionManager.removeChatListener(listener);
    }

    public void emitNewMessage(String message, String userId) {
        chatConnectionManager.emitNewMessage(message, userId);
    }

    public interface OnChatListener {
        void onNewMessage(ChatMessage message);
        void onUsersList(List<ChatUser> users);
    }
}
