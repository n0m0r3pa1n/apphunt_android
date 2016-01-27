package com.apphunt.app.api.apphunt.clients.sockets;

import com.apphunt.app.api.apphunt.clients.sockets.SocketConnectionManager.OnRefreshListener;
import com.apphunt.app.api.apphunt.models.users.HistoryEvent;
import com.apphunt.app.utils.GsonInstance;
import com.crashlytics.android.Crashlytics;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nmp on 16-1-27.
 */
public class HistoryConnectionManager extends BaseConnectionManager {
    private List<OnRefreshListener> listeners = new ArrayList<>();

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

    public HistoryConnectionManager(Socket socket) {
        super(socket);
        socket.on("refresh", onRefresh);
        socket.on("unseen events", onUnseenEvents);
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

    public void emitAddUserToHistory(String userId) {
        connectIfNotConnected();
        try {
            socket.emit("add user", userId);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void emitLastSeenEventId(String userId, String eventId, String date) {
        connectIfNotConnected();
        try {
            socket.emit("last seen event", userId, eventId, date);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void notifyListenersForUnseenEvents(List<String> ids) {
        for(int i=0; i < listeners.size(); i++) {
            listeners.get(i).onUnseenEvents(ids);
        }
    }
}
