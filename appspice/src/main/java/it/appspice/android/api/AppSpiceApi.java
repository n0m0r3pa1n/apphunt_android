package it.appspice.android.api;

import it.appspice.android.api.models.Event;
import it.appspice.android.api.models.User;
import it.appspice.android.api.models.Variable;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by nmp on 1/14/15.
 */
public interface AppSpiceApi {
    @POST("/users")
    void createUser(@Body User user, Callback<Response> cb);

    @POST("/events")
    void createEvent(@Body Event event, Callback<Response> cb);

    @GET("/variables/{variable}")
    void getVariable(@Path("variable") String variable, @Query("userId") String userId, @Query("appId") String appId, Callback<Variable> callback);
}
