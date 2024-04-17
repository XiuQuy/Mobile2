package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.InformationMovie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaylistItemService {
    @POST("api/WatchListItem/check-all/{userId}")
    Call<List<Integer>> checkMovieInAllPlaylist(
            @Path("userId") int userId,
            @Header("Authorization") String token,
            @Body InformationMovie informationMovie
    );
}
