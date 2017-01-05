package com.fantasticfour.esurvey.Interface;

import com.fantasticfour.esurvey.Objects.LoginResponse;
import com.fantasticfour.esurvey.Objects.Response;
import com.fantasticfour.esurvey.Objects.ServerResponse;
import com.fantasticfour.esurvey.Objects.Survey;
import com.fantasticfour.esurvey.Objects.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestInterface {
    @POST("register")
    Call<User> register(@Body User user);

    @GET("test")
    Call<User> getTest();

    @POST("login")
    Call<User> login(@Body User user);

    @GET("user/{id}/surveys")
    Call<ServerResponse> getSurveys(@Path("id") int id);

    @POST("answer")
    Call<Integer> sendResponse(@Body Response response);
}
