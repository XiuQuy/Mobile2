package com.example.appxemphim.util;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.appxemphim.data.remote.PlaylistItemService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;

import java.io.IOException;

public class AddMovieToPlaylistLoader extends AsyncTaskLoader<Integer> {
    private final PlaylistItemService playlistItemService;

    public AddMovieToPlaylistLoader(Context context) {
        super(context);
        playlistItemService = ServiceApiBuilder.buildUserApiService(PlaylistItemService.class);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Integer loadInBackground() {
        try {
            // Thực hiện truy vấn dữ liệu từ Retrofit
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}