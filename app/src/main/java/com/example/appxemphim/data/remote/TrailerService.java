package com.example.appxemphim.data.remote;

import com.example.appxemphim.ui.viewmodel.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface TrailerService {
    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getTrailers(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}

