package com.apphunt.app.api.apphunt.requests;

import com.android.volley.Response;

/**
 * Created by nmp on 15-5-9.
 */
public abstract class BaseGetRequest<T> extends BaseGsonRequest<T> {

    public BaseGetRequest(String url, Response.ErrorListener listener) {
        super(Method.GET, url, listener);
    }

    public abstract Class<T> getParsedAppClass();

    public abstract void deliverResponse(T response);

}
