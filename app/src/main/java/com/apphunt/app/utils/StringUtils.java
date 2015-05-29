package com.apphunt.app.utils;

import android.text.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by nmp on 15-5-28.
 */
public class StringUtils {
    public static String decodeString(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String htmlDecodeString(String str) {
        return Html.fromHtml(str).toString();
    }
}
