package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.ReviewVideo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReviewVideoService {
    @GET("api/ReviewVideo/all/{movieId}")
    Call<List<ReviewVideo>> getAllReviewVideoByMovieId(
            @Path("movieId") String movieId);
}
