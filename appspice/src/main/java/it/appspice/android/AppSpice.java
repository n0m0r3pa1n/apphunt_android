package it.appspice.android;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

import it.appspice.android.api.AppSpiceApiClient;
import it.appspice.android.api.Callback;
import it.appspice.android.api.EmptyCallback;
import it.appspice.android.api.models.Event;
import it.appspice.android.api.models.User;
import it.appspice.android.api.models.Variable;
import it.appspice.android.api.models.VariableProperties;
import it.appspice.android.helpers.Constants;
import it.appspice.android.helpers.MetaDataHelper;
import it.appspice.android.listeners.OnVariablePropertiesListener;
import it.appspice.android.listeners.UserTrackingListener;
import it.appspice.android.providers.UniqueIdProvider;
import retrofit.client.Response;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public class AppSpice {
    private static final String TAG = AppSpice.class.getSimpleName();
    private static String appId;

    private Context context;

    private static AppSpice instance;

    private static UserTrackingListener userTrackingListener = new UserTrackingListener() {
        @Override
        public void onTrackingEnabled() {
        }

        @Override
        public void onTrackingDisabled() {
        }
    };

    private static boolean isUserTrackingEnabled = true;

    private static String advertisingId;

    private AppSpice(Context context) {
        this.context = context;
    }

    private static void newInstance(Context context) {
        if (instance == null) {
            instance = new AppSpice(context);
        }
        instance.context = context;
    }

    /**
     * Initializing the AppSpice SDK with providing AppSpiceId and AppId in the constructor.
     *
     * @param appContext Application's Context
     * @param appSpiceId Developer's appSpiceId
     * @param appId      Application's appId
     */
    public static void init(Context appContext, String appSpiceId, String appId) {
        newInstance(appContext);

        instance.initAppSpice(appSpiceId, appId);
    }

    private void initAppSpice(String appSpiceId, String appId) {

        if (TextUtils.isEmpty(appSpiceId) || TextUtils.isEmpty(appId)) {
            Log.e(TAG, "APP_SPICE_ID or APP_SPICE_APP_ID is empty.");
            return;
        }

        AppSpice.appId = appId;

        UniqueIdProvider.getAdvertisingId(context, new UniqueIdProvider.AdvertisingIdListener() {
            @Override
            public void onAdvertisingIdReady(String advertisingId, boolean isLimitAdTrackingEnabled) {
                if (isLimitAdTrackingEnabled) {
                    isUserTrackingEnabled = false;
                    userTrackingListener.onTrackingDisabled();
                } else {
                    isUserTrackingEnabled = true;
                    AppSpice.advertisingId = advertisingId;
                    AppSpiceApiClient.getClient().createUser(new User(advertisingId), new EmptyCallback<Response>());
                    track("appSpice", "appStart");
                    userTrackingListener.onTrackingEnabled();
                }
            }

            @Override
            public void onAdvertisingIdError() {
                userTrackingListener.onTrackingDisabled();
                isUserTrackingEnabled = false;
            }
        });
    }

    public static void init(Context appContext) {
        newInstance(appContext);

        String appSpiceId = MetaDataHelper.getMetaData(appContext, Constants.KEY_APP_SPICE_ID);
        String appId = MetaDataHelper.getMetaData(appContext, Constants.KEY_APP_ID);

        instance.initAppSpice(appSpiceId, appId);
    }

    private static void track(Event event) {
        if (isUserTrackingEnabled) {
            event.getData().put("advertisingId", advertisingId);
        }
        AppSpiceApiClient.getClient().createEvent(event, new EmptyCallback<Response>());
    }

    public static void track(String namespace, String name) {
        track(new Event(namespace, name, appId));
    }

    public static void track(String namespace, String name, Map<String, Object> data) {
        track(new Event(namespace, name, appId, data));
    }

    public static void getVariableProperties(String variable, final OnVariablePropertiesListener onVariablePropertiesListener) {
        if (isUserTrackingEnabled) {
            AppSpiceApiClient.getClient().getVariable(variable, advertisingId, appId, new Callback<Variable>() {
                @Override
                public void success(Variable variable, Response response) {
                    VariableProperties variableProperties = VariableProperties.fromVariable(variable);
                    onVariablePropertiesListener.onPropertiesReady(variableProperties);
                }
            });
        }
    }

    public static void setUserTrackingPreferenceListener(UserTrackingListener listener) {
        userTrackingListener = listener;
    }

}
