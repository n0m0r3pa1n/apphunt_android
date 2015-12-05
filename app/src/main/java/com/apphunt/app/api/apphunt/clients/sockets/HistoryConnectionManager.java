package com.apphunt.app.api.apphunt.clients.sockets;

import android.util.Log;

import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.constants.Constants;
import com.apphunt.app.utils.GsonInstance;
import com.crashlytics.android.Crashlytics;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HistoryConnectionManager {
    public static final String TAG = HistoryConnectionManager.class.getSimpleName();

    private static HistoryConnectionManager historyConnectionManager;

    private List<OnRefreshListener> listeners = new ArrayList<>();

    private Socket socket;
    private boolean isConnecting = false;

    {
        try {
            socket = IO.socket(Constants.BASE_SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.d(TAG, "instance initializer " + e);
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private Emitter.Listener onRefresh = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                HistoryEvent event = GsonInstance.fromJson(data.getString("event"), HistoryEvent.class);
                notifyListenersForRefresh(event);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onUnseenEvents = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            Type listOfString = new TypeToken<List<String>>(){}.getType();
            try {
                List<String> eventIds = GsonInstance.fromJson(data.getString("events"), listOfString);
                notifyListenersForUnseenEvents(eventIds);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
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
        connectIfNotConnected();
        try {
            socket.emit("add user", userId);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }


    private void connectIfNotConnected() {
        if(socket != null && !socket.connected() && !isConnecting) {
            isConnecting = true;
            socket.on("refresh", onRefresh);
            socket.on("unseen events", onUnseenEvents);
            socket.connect();
        }
    }

    public void emitLastSeenId(String userId, String eventId, String date) {
        connectIfNotConnected();
        try {
            socket.emit("last seen event", userId, eventId, date);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void addRefreshListener(OnRefreshListener listener) {
        connectIfNotConnected();
        listeners.add(listener);
    }

    public void removeRefreshListener(OnRefreshListener listener) {
        listeners.remove(listener);
        if(listeners.size() == 0 && socket.connected()) {
            isConnecting = false;
            socket.disconnect();
            socket.off("refresh", onRefresh);
            socket.off("unseen events", onUnseenEvents);
        }
    }

    public void notifyListenersForRefresh(HistoryEvent event) {
        for(int i=0; i < listeners.size(); i++) {
            listeners.get(i).onRefresh(event);
        }
    }

    public void notifyListenersForUnseenEvents(List<String> ids) {
        for(int i=0; i < listeners.size(); i++) {
            listeners.get(i).onUnseenEvents(ids);
        }
    }


    public interface OnRefreshListener {
        void onRefresh(HistoryEvent event);
        void onUnseenEvents(List<String> ids);
    }
}
