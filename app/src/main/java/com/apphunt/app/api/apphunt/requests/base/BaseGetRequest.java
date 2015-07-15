package com.apphunt.app.api.apphunt.requests.base;

import com.android.volley.Response;

public abstract class BaseGetRequest<T> extends BaseGsonRequest<T> {

    public BaseGetRequest(String url, Response.ErrorListener listener) {
        super(Method.GET, url, listener);
    }

    public abstract Class<T> getParsedClass();

    public abstract void deliverResponse(T response);
}
