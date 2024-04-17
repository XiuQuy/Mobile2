package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    @POST("api/User/login")
    Call<User> checkLogin();

    @POST("api/User/login-google")
    Call<User> checkLoginGoogle(@Body String googleIdToken);

    @POST("api/User/login-facebook")
    Call<User> checkLoginFacebook(@Body String firebaseAuthenFbIdToken);
}
