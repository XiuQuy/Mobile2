package com.example.appxemphim.data.remote;

import com.example.appxemphim.ui.viewmodel.CreditsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CreditTvService {
    @GET("tv/{tv_id}/credits")
    Call<CreditsResponse> getTvCredits(
            @Path("tv_id") int movieId,
            @Query("api_key") String apiKey
    );
}
