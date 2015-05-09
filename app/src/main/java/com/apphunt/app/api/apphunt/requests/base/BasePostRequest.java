package com.apphunt.app.api.apphunt.requests.base;

import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.apphunt.app.utils.GsonInstance;

import java.io.UnsupportedEncodingException;


public abstract class BasePostRequest<T> extends BaseGsonRequest<T> {
    private static final String PROTOCOL_CHARSET = "utf-8";

    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    private final String requestBody;

    public BasePostRequest(String url, Object body, Response.ErrorListener listener) {
        super(Method.POST, url, listener);
        requestBody = GsonInstance.toJson(body);
    }

    public abstract Class<T> getParsedAppClass();

    public abstract void deliverResponse(T response);

    protected String getRequestBody(Object object) {
        return GsonInstance.toJson(object);
    };

    @Override
    public byte[] getBody() {
        try {
            return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, PROTOCOL_CHARSET);
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
