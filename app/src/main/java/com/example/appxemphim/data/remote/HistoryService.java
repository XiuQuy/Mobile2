package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.History;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.DELETE;
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

    @DELETE("api/History/delete-one/{historyId}/{userId}")
    Call<DeleteResponse> deleteOneHistory(
            @Path("historyId") int historyId,
            @Path("userId") int userId,
            @Header("Authorization") String token);
        //delete-one
    @DELETE("api/History/delete-last-hour/{userId}")
    Call<DeleteResponse> deleteLastHourHistory(
            @Path("userId") int userId,
            @Header("Authorization")
            String token);

    @DELETE("api/History/delete-last-day/{userId}")
    Call<DeleteResponse> deleteLastDayHistory(
            @Path("userId") int userId,
            @Header("Authorization")
            String token);

    @DELETE("api/History/delete-last-week/{userId}")
    Call<DeleteResponse> deleteLastWeekHistory(
            @Path("userId") int userId,
            @Header("Authorization")
            String token);

    @DELETE("api/History/delete-last-month/{userId}")
    Call<DeleteResponse> deleteLastMonthHistory(
            @Path("userId") int userId,
            @Header("Authorization")
            String token);

    @DELETE("api/History/delete-all/{userId}")
    Call<DeleteResponse> deleteAll(
            @Path("userId") int userId,
            @Header("Authorization")
            String token);

    @POST("api/History/add")
    Call<History> addOrUpdateHistory(
            @Body History history,
            @Header("Authorization") String token);


}



