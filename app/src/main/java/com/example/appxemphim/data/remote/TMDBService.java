package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.TMDBGenreListResponse;
import com.example.appxemphim.model.TMDBSearchMovieResponse;
import com.example.appxemphim.model.TMDBSearchTVResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("search/movie")
    Call<TMDBSearchMovieResponse> searchMovie(
            @Query("query") String query,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int page,
            @Query("primary_release_year") String year,
            @Query("api_key") String apiKey);

    @GET("search/tv")
    Call<TMDBSearchTVResponse> searchTV(
            @Query("query") String query,
            @Query("include_adult") boolean includeAdult,
            @Query("language") String language,
            @Query("page") int page,
            @Query("first_air_date_year") int year,
            @Query("api_key") String apiKey);

    @GET("genre/tv/list")
    Call<TMDBGenreListResponse> listGenreTV(
            @Query("language") String language,
            @Query("api_key") String apiKey);

    @GET("genre/movie/list")
    Call<TMDBGenreListResponse> listGenreMovie(
            @Query("language") String language,
            @Query("api_key") String apiKey);
}
