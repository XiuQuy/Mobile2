package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.ChangeInfoUserDTO;
import com.example.appxemphim.model.ChangePasswordDTO;
import com.example.appxemphim.model.ForgotDTO;
import com.example.appxemphim.model.User;
import com.example.appxemphim.model.UserLogin;
import com.example.appxemphim.model.UserRegister;
import com.example.appxemphim.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
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
    Call<User> checkLoginFacebook(@Body String firebaseAuthenFbIdToken);

    @POST("api/User/change-password")
    Call<Void> changePassword(@Body ChangePasswordDTO changePasswordDTO,
                                @Header("Authorization") String token);

    @POST("api/User/change-info")
    Call<User> changeName(@Body ChangeInfoUserDTO changeInfoUserDTO,
                                @Header("Authorization") String token);


    @POST("api/User/forgot-password-request")
    Call<Void> forgotPasswordRequest(@Body ForgotDTO forgotDTO);

    @POST("api/User/forgot-password-checkcode")
    Call<Void> forgotPasswordCheckCode(@Body ForgotDTO forgotDTO);


    @POST("api/User/forgot-password-change")
    Call<Void> forgotPasswordChange(@Body ForgotDTO forgotDTO);
}
