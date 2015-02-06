package it.appspice.android.api;

import retrofit.client.Response;

/**
 * Created by nmp on 1/14/15.
 */
public class EmptyCallback<T> extends Callback<T> {
    @Override
    public void success(T t, Response response) {

    }
}
