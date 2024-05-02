package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.YoutubeChannelResponse;
import com.example.appxemphim.model.YoutubeVideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeService {
    @GET("channels")
    Call<YoutubeChannelResponse> getChannelInfo(
            @Query("part") String[] part,
            @Query("id") String[] videoId,
            @Query("key") String apiKey
    );

}
