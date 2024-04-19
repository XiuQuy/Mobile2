package com.example.appxemphim.data.remote;
import com.example.appxemphim.ui.viewmodel.DetailTvResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DetailTvService {
    @GET("tv/{tv_id}")
    public Call<DetailTvResponse> getDetailTv(
            @Path("tv_id") int id,
            @Query("api_key") String apiKey
    );
}
