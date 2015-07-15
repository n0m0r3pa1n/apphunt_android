package com.apphunt.app.api.apphunt.requests.base;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.apphunt.app.utils.GsonInstance;

import java.io.UnsupportedEncodingException;


public abstract class BasePostRequest<T> extends BaseGsonRequest<T> {
    public static final String TAG = BasePostRequest.class.getSimpleName();
    private static final String PROTOCOL_CHARSET = "utf-8";

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private String body = null;

    public BasePostRequest(String url, Object body, Response.ErrorListener listener) {
        super(Method.POST, url, listener);
        if(body != null) {
            this.body = GsonInstance.toJson(body);
        }
        Log.d(TAG, "BasePostRequest ");
    }

    public abstract Class<T> getParsedClass();

    public abstract void deliverResponse(T response);

    protected String getRequestBody(Object object) {
        return GsonInstance.toJson(object);
    };

    @Override
    public byte[] getBody() {
        try {
            return body == null ? null : body.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    body, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }
}
