package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.TMDBSearchMovieResponse;
import com.example.appxemphim.model.TMDBSearchTVResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("search/movie")
    public Call<TMDBSearchMovieResponse> searchMovie(
            @Query("query") String query,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int page,
            @Query("year") String year,
            @Query("api_key") String apiKey);

    @GET("search/tv")
    public Call<TMDBSearchTVResponse> searchTV(
            @Query("query") String query,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int page,
            @Query("year") String year,
            @Query("api_key") String apiKey);
}
