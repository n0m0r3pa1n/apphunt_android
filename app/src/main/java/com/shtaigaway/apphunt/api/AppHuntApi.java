package com.shtaigaway.apphunt.api;

import com.shtaigaway.apphunt.api.models.User;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;


public interface AppHuntApi {
    @POST("/users")
    void createUser(@Body User user, Callback<Response> cb);
}
