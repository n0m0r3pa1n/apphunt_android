package it.appspice.android.helpers;

/**
 * Created by NaughtySpirit
 * Created on 20/Aug/2014
 */
public class Constants {

//    public static final String API_ENDPOINT = "ws://10.0.2.2:8080/primus";
//    public static final String API_ENDPOINT = "ws://192.168.1.21:8080/primus";
    public static final String API_ENDPOINT = "ws://appspice-server.herokuapp.com/primus";
    public static final String API_PROTOCOL = "ws";

    public static final String KEY_APP_SPICE_ID =  "APP_SPICE_ID";
    public static final String KEY_APP_ID =  "APP_SPICE_APP_ID";
    public static final String KEY_USER_ID =  "USER_ID";
    public static final String KEY_IS_READY = "IS_READY";

    public static final int INSTALLED_APPS_SERVICE_INTERVAL = 1 * 60 * 1000;

    public enum AdTypes {FullScreen, Interstitial, Wall, Grid, Banner}
}
