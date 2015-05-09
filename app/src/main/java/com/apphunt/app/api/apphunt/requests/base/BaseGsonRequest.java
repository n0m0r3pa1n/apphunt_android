package com.apphunt.app.api.apphunt.requests.base;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.apphunt.app.utils.GsonInstance;

public abstract class BaseGsonRequest<T> extends Request<T> {
    public static String BASE_URL = "http://apphunt.herokuapp.com";

    public BaseGsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(GsonInstance.fromJson(json, getParsedAppClass()),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch(Exception e) {

            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    public abstract Class<T> getParsedAppClass();

    @Override
    public abstract void deliverResponse(T response);

}
