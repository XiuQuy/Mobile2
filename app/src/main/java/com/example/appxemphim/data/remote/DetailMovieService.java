package com.example.appxemphim.data.remote;
import com.example.appxemphim.ui.viewmodel.DetailMovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DetailMovieService {
@GET("movie/{id}")
    public Call<DetailMovieResponse> getDetailMovie(

            @Path("id") int id,
            @Query("language") String language,
            @Query("api_key") String apiKey
);

}
