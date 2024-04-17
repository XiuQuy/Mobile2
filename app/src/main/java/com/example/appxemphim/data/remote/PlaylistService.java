package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.InformationMovie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PlaylistService {
    @POST("api/WatchList/add-to-new-watchlist")
    Call<List<Integer>> addWithOneItem(
            @Header("Authorization") String token,
            @Body InformationMovie informationMovie
    );
}
