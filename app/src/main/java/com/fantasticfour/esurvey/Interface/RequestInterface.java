package com.fantasticfour.esurvey.Interface;

import com.fantasticfour.esurvey.Objects.LoginResponse;
import com.fantasticfour.esurvey.Objects.Response;
import com.fantasticfour.esurvey.Objects.ServerResponse;
import com.fantasticfour.esurvey.Objects.Survey;
import com.fantasticfour.esurvey.Objects.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestInterface {
    @POST("android/register")
    Call<User> register(@Body User user);

    @GET("android/test")
    Call<User> getTest();

    @POST("android/login")
    Call<User> login(@Body User user);

    @GET("android/user/{id}/surveys")
    Call<ServerResponse> getSurveys(@Path("id") int id);

    @POST("android/answer")
    Call<Integer> sendResponse(@Body Response response);

    @GET("android/speech/{id}")
    Call<ResponseBody> downloadSpeech(@Path("id") int id);
}
