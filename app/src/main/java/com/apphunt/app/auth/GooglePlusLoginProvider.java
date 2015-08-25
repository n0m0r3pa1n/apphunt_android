package com.apphunt.app.auth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 7/31/15.
 * *
 * * NaughtySpirit 2015
 */
public class GooglePlusLoginProvider extends BaseLoginProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {
    private static final String TAG = GooglePlusLoginProvider.class.getSimpleName();
    public static final String PROVIDER_NAME = "google-plus";

    private GoogleApiClient googleApiClient;
    private OnResultListener listener;

    public interface OnResultListener {
        void onFriendsReceived(ArrayList<Person> people);
    }

    public GooglePlusLoginProvider(Activity activity) {
        super(activity);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Plus.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        googleApiClient.connect();
    }

    public void loadFriends(OnResultListener listener) {
        Log.e(TAG, "loadFriends");
        this.listener = listener;
        Plus.PeopleApi.loadVisible(googleApiClient, "me").setResultCallback(this);
    }

    @Override
    public void logout() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient);
            googleApiClient.disconnect();
        }
        super.logout();
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Connected " + (listener != null) );

        if (listener != null) {
            Plus.PeopleApi.loadVisible(googleApiClient, "me").setResultCallback(this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.toString());
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        ArrayList<Person> people = new ArrayList<>();

        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                Log.e(TAG, "Friends count: " + count);
                for (int i = 0; i < count; i++) {
                    people.add(personBuffer.get(i));
                    Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e(TAG, "Error requesting visible circles: " + loadPeopleResult.getStatus());
        }

        listener.onFriendsReceived(people);
    }
}
