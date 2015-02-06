package com.apphunt.app.api;

import retrofit.client.Response;

public class EmptyCallback<T> extends Callback<T> {
    @Override
    public void success(T t, Response response) {

    }
}
