package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.History;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HistoryService {
    @GET("api/History/{limit}/{userId}")
    Call<List<History>> getHistory(
            @Path("limit") int limit,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @GET("api/History/all/{userId}")
    Call<List<History>> getAllHistory(
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @POST("api/History/add")
    Call<History> addOrUpdateHistory(
            @Body History history,
            @Header("Authorization") String token);


}
