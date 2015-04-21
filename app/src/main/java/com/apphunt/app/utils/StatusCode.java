package com.apphunt.app.utils;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 4/21/15.
 */
public enum StatusCode {
    SUCCESS(200), CONFLICT(409);

    private int code;

    private StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
