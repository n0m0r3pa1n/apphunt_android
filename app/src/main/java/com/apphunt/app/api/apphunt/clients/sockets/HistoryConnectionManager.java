package com.apphunt.app.api.apphunt.clients.sockets;

import android.util.Log;

import com.apphunt.app.constants.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HistoryConnectionManager {
    public static final String TAG = HistoryConnectionManager.class.getSimpleName();

    private static HistoryConnectionManager historyConnectionManager;

    private List<OnRefreshListener> listeners = new ArrayList<>();

    private Socket socket;
    {
        try {
            socket = IO.socket(Constants.BASE_SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.d(TAG, "instance initializer " + e);
            e.printStackTrace();
        }
    }

    private Emitter.Listener onRefresh = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            notifyListeners();
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "onConnect");
        }
    };

    private HistoryConnectionManager() {}

    public static HistoryConnectionManager getInstance() {
        if(historyConnectionManager == null) {
            historyConnectionManager = new HistoryConnectionManager();
        }

        return historyConnectionManager;
    }

    public void emitAddUser(String userId) {
        if(!socket.connected()) {
            socket.on("refresh", onRefresh);
            socket.on("connect", onConnect);
            socket.connect();
        }

        socket.emit("add user", userId);
    }

    public void addRefreshListener(OnRefreshListener listener) {
        listeners.add(listener);
    }

    public void removeRefreshListener(OnRefreshListener listener) {
        listeners.remove(listener);
        if(listeners.size() == 0 && socket.connected()) {
            socket.disconnect();
            socket.off("refresh", onRefresh);
        }
    }

    public void notifyListeners() {
        for(int i=0; i < listeners.size(); i++) {
            listeners.get(i).onRefresh();
        }
    }


    public interface OnRefreshListener {
        void onRefresh();
    }
}
