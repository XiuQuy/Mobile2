package com.example.appxemphim.util;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.appxemphim.data.remote.PlaylistItemService;
import com.example.appxemphim.data.remote.PlaylistService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.Playlist;
import com.example.appxemphim.model.PlaylistWithOneItemDTO;

import retrofit2.Response;

public class AddWithNewPlaylistLoader extends AsyncTaskLoader<Playlist> {
    private final PlaylistService playlistService;
    private PlaylistWithOneItemDTO playlist;

    public AddWithNewPlaylistLoader(Context context, PlaylistWithOneItemDTO playlist) {
        super(context);
        playlistService = ServiceApiBuilder.buildUserApiService(PlaylistService.class);
        this.playlist = playlist;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Playlist loadInBackground() {
        try {
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKV1RTZXJ2aWNlQWNjZXNzVG9rZW4iLCJqdGkiOiI5YTRhYmM2OS1hNWRkLTQzNGYtOWFmZC0xMjZkZDEyMzI0OGEiLCJpYXQiOiIxMi8wNC8yMDI0IDI6MTU6NTYgQ0giLCJVc2VySWQiOiIxIiwiRW1haWwiOiJkb2xlaHV5MjIyQGdtYWlsLmNvbSIsImV4cCI6MTcxMzc5NTM1NiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0OTg3MCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDk4NzAifQ.bygdYjiAlUIsxm2104EoRQfuRKfQQfnJCRNczwDL6B8";
            Response<Playlist> newPlaylistResponse = playlistService.addWithOneItem("Bearer "+token, playlist).execute();
            return newPlaylistResponse.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
