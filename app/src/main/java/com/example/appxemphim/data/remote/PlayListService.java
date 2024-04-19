package com.example.appxemphim.data.remote;


import com.example.appxemphim.model.DeleteResponse;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface PlayListService {
    @GET("api/WatchList/{limit}/{userId}")
    Call<List<Playlist>> getPlaylist(
            @Path("limit") int limit,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @GET("api/WatchList/all/{userId}")
    Call<List<Playlist>> getAllPlaylist(
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @DELETE("api/WatchList/delete-one/{watchListId}/{userId}")
    Call<DeleteResponse> deleteonePlaylist(
            @Path("watchListId") int watchListId,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @GET("api/WatchListItem/{limit}/{watchListId}/{userId}")
    Call<List<PlaylistItem>> getPlaylistItem(
            @Path("limit") int limit,
            @Path("watchListId") int watchListId,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @GET("api/WatchListItem/all/{watchListId}/{userId}")
    Call<List<PlaylistItem>> getAllPlaylistItem(
            @Path("watchListId") int watchListId,
            @Path("userId") int userId,
            @Header("Authorization") String token);

    @DELETE("api/WatchListItem/delete-one/{watchListItemId}/{userId}")
    Call<DeleteResponse> deleteOnePlaylistItem(
            @Path("watchListItemId") int watchListItemId,
            @Path("userId") int userId,
            @Header("Authorization") String token);
}
