package com.apphunt.app.api.apphunt.clients.sockets;

import android.util.Log;

import com.apphunt.app.api.apphunt.clients.sockets.SocketConnectionManager.OnChatListener;
import com.apphunt.app.api.apphunt.models.chat.ChatUser;
import com.apphunt.app.api.apphunt.models.users.User;
import com.apphunt.app.utils.GsonInstance;
import com.crashlytics.android.Crashlytics;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nmp on 16-1-27.
 */
public class ChatConnectionManager extends BaseConnectionManager {
    public static final String TAG = ChatConnectionManager.class.getSimpleName();
    private List<OnChatListener> listenerList = new ArrayList<>();

    private Emitter.Listener onNewTopHuntersMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            Log.d(TAG, "call: " + data.toString());
            try {
//                ChatMessage event = GsonInstance.fromJson(data.toString(), ChatMessage.class);
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onHuntersList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            List<ChatUser> users = new ArrayList<>();
            try {
                JSONArray usersJson = data.getJSONArray("users");
                int size = usersJson.length();
                for (int i = 0; i < size; i++) {
                    ChatUser user = GsonInstance.fromJson(usersJson.getString(i), ChatUser.class);
                    users.add(user);
                }
                notifyUserListeners(users);
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    };

    public ChatConnectionManager(Socket socket) {
        super(socket);
        socket.on("new top hunters message", onNewTopHuntersMessage);
        socket.on("hunters list", onHuntersList);
    }

    public void addChatListener(OnChatListener listener) {
        listenerList.add(listener);
        connectIfNotConnected();
    }

    public void removeChatListener(OnChatListener listener) {
        listenerList.remove(listener);
    }

    public void emitAddUserToTopHuntersChat(User user) {
        socket.emit("add user to top hunters chat", user);
    }

    public void emitNewMessage(String message, String userId) {
        socket.emit("new top hunters message", message, userId);
    }

    public void leaveTopHuntersRoom() {
        socket.emit("leave top hunters room");
    }

    public void notifyUserListeners(List<ChatUser> users) {
        int size = listenerList.size();
        for (int i = 0; i < size; i++) {
            listenerList.get(i).onUsersList(users);
        }
    }
}
