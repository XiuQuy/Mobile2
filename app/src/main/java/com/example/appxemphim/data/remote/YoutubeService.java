package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.VideoYoutubeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeService {
    @GET("videos")
    Call<VideoYoutubeResponse> getVideoInfo(
            @Query("part") String part,
            @Query("id") String videoId,
            @Query("key") String apiKey
    );
}
