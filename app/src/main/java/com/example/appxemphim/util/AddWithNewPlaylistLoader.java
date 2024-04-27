package com.example.appxemphim.util;

import android.content.Context;
import android.content.SharedPreferences;

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
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            Response<Playlist> newPlaylistResponse = playlistService.addWithOneItem("Bearer "+token, playlist).execute();
            return newPlaylistResponse.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
