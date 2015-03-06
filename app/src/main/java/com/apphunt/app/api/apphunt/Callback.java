package com.apphunt.app.api.apphunt;


import android.util.Log;

import retrofit.RetrofitError;

/**
 * Created by nmp on 1/14/15.
 */
public abstract class Callback<T> implements retrofit.Callback<T> {
    @Override
    public void failure(RetrofitError error) {
        Log.d("API_ERROR", error.toString());
    }
}
