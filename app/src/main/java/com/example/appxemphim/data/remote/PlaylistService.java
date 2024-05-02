package com.example.appxemphim.data.remote;

import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;
import com.example.appxemphim.model.PlaylistWithOneItemDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaylistService {
    @GET("api/WatchList/{limit}/{userId}")
    Call<List<Playlist>> getPlaylist(
            @Path("limit") int limit,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @GET("api/WatchList/all/{userId}")
    Call<List<Playlist>> getPlaylist(
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @DELETE("api/WatchList/delete-one/{watchListId}/{userId}")
    Call<DeleteResponse> deleteonePlaylist(
            @Path("watchListId") int watchListId,
            @Path("userId") int userId,
            @Header("Authorization") String token);

}
