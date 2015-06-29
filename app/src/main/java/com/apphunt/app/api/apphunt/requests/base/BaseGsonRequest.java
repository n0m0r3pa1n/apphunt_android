package com.apphunt.app.api.apphunt.requests.base;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.apphunt.app.utils.GsonInstance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class BaseGsonRequest<T> extends Request<T> {
    public static final String TAG = BaseGsonRequest.class.getSimpleName();
    public static String BASE_URL = "http://apphunt-dev.herokuapp.com";
    private NetworkResponse networkResponse;

    public BaseGsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, encodeUrl(url), listener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        networkResponse = response;
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(GsonInstance.fromJson(json, getParsedAppClass()),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch(Exception e) {
            Log.d(TAG, "parseNetworkResponse " + e);
            e.printStackTrace();
            return null;
        }
    }

    protected NetworkResponse getRawResponse() {
        return networkResponse;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    public abstract Class<T> getParsedAppClass();

    @Override
    public abstract void deliverResponse(T response);

    private static String encodeUrl(String url) {
        return url.replaceAll(" ", "%20");
    }
}
