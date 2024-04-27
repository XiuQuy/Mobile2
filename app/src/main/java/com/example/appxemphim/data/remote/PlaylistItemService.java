package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.PlaylistItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    @DELETE("api/WatchListItem/delete-one/{watchListId}/{movieId}/{tag}/{userId}")
    Call<Void> deleteWithInformationMovie(
            @Path("userId") int userId,
            @Path("watchListId") int watchListId,
            @Path("movieId") String movieId,
            @Path("tag") String tag,
            @Header("Authorization") String token
    );
    @POST("api/WatchListItem/add/{userId}")
    Call<PlaylistItem> add(
            @Path("userId") int userId,
            @Body PlaylistItem playlistItem,
            @Header("Authorization") String token
    );
}
