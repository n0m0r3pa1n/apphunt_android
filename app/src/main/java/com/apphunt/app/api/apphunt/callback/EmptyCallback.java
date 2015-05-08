package com.apphunt.app.api.apphunt.callback;

import retrofit.client.Response;

public class EmptyCallback<T> extends Callback<T> {
    @Override
    public void success(T t, Response response) {

    }
}
