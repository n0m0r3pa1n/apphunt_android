package com.apphunt.app.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.lang.reflect.Type;

public class GsonInstance {

    private static Gson sGson;

    public static void init() {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").disableHtmlEscaping();
        sGson = builder.create();
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return sGson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return sGson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static String toJson(Serializable dto) {
        return sGson.toJson(dto);
    }

    public static String toJson(Object object) {
        return sGson.toJson(object);
    }
}