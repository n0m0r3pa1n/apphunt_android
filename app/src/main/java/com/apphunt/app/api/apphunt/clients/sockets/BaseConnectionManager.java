package com.apphunt.app.api.apphunt.clients.sockets;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by nmp on 16-1-27.
 */
public class BaseConnectionManager {
    protected boolean isConnecting = false;
    protected Socket socket;
//    private List<OnSocketConnectionStartListener> listeners = new ArrayList<>();

    public BaseConnectionManager(Socket socket) {
        this.socket = socket;
    }

    public void connectIfNotConnected() {
        if(socket != null && !socket.connected() && !isConnecting) {
            isConnecting = true;
//            notifyConnectionListeners();
            socket.connect();
        }
    }

//    public void addConnectionListener(OnSocketConnectionStartListener listener) {
//        listeners.add(listener);
//    }
//
//    public void removeConnectionListener(OnSocketConnectionStartListener listener) {
//        listeners.remove(listener);
//    }
//
//    public void notifyConnectionListeners() {
//        for(OnSocketConnectionStartListener listener : listeners) {
//            listener.onConnectionStart(socket);
//        }
//    }
//
//    public interface OnSocketConnectionStartListener {
//        void onConnectionStart(Socket socket);
//    }
}
