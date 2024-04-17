package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.UserLogin;
import com.example.appxemphim.model.UserRegister;
import com.example.appxemphim.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {
    @POST("api/User/login")
    Call<UserResponse> checkLogin(
            @Body UserLogin user);

    @POST("api/User/register")
    Call<UserResponse> register(
            @Body UserRegister user);

    @POST("api/User/login-google")
    Call<User> checkLoginGoogle(@Body String googleIdToken);

    @POST("api/User/login-facebook")
}
