package com.example.appxemphim.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.HistoryService;
import com.example.appxemphim.data.remote.PlaylistItemService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.History;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.PlaylistItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AddHistoryLoader extends AsyncTaskLoader<Void> {
    private final HistoryService historyService;
    History history;

    public AddHistoryLoader(Context context, History history) {
        super(context);
        historyService = ServiceApiBuilder.buildUserApiService(HistoryService.class);
        this.history = history;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Void loadInBackground() {
        try {
            Log.i("ADD_HISTORY","ADD HISTORY");
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            historyService.addOrUpdateHistory(history, "Bearer " + token).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
