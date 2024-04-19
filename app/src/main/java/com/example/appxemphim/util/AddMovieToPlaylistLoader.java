package com.example.appxemphim.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.PlaylistItemService;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.model.InformationMovie;
import com.example.appxemphim.model.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AddMovieToPlaylistLoader extends AsyncTaskLoader<List<String>> {
    private final PlaylistItemService playlistItemService;
    ArrayList<Integer> listAdd;
    ArrayList<Integer> listRemove;
    InformationMovie informationMovie;
    List<String> responseMessage;

    public AddMovieToPlaylistLoader(Context context, ArrayList<Integer> listAdd, ArrayList<Integer> listRemove, InformationMovie informationMovie) {
        super(context);
        playlistItemService = ServiceApiBuilder.buildUserApiService(PlaylistItemService.class);
        this.listAdd = listAdd;
        this.listRemove = listRemove;
        this.informationMovie = informationMovie;
        this.responseMessage = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {
        try {
            String addToString = getContext().getString(R.string.add_to)+" ";
            String removeFromString = getContext().getString(R.string.remove_from)+" ";
            String successfullyString = " "+getContext().getString(R.string.successfully);
            String failedString = " "+getContext().getString(R.string.failure);
            int userId = 1;
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKV1RTZXJ2aWNlQWNjZXNzVG9rZW4iLCJqdGkiOiI5YTRhYmM2OS1hNWRkLTQzNGYtOWFmZC0xMjZkZDEyMzI0OGEiLCJpYXQiOiIxMi8wNC8yMDI0IDI6MTU6NTYgQ0giLCJVc2VySWQiOiIxIiwiRW1haWwiOiJkb2xlaHV5MjIyQGdtYWlsLmNvbSIsImV4cCI6MTcxMzc5NTM1NiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0OTg3MCIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDk4NzAifQ.bygdYjiAlUIsxm2104EoRQfuRKfQQfnJCRNczwDL6B8";
            if(!listAdd.isEmpty()){
                PlaylistItem playlistItem = new PlaylistItem();
                playlistItem.setInformationMovie(informationMovie);
                for (Integer playlistId: listAdd) {
                    playlistItem.setWatchListId(playlistId);
                    Response<PlaylistItem> responseAdd = playlistItemService.add(userId, playlistItem, "Bearer "+token).execute();
                    if(responseAdd.code() == 200){
                        responseMessage.add(addToString + playlistId + successfullyString);
                    }else {
                        responseMessage.add(addToString + playlistId + failedString);
                    }
                }
            }
            if(!listRemove.isEmpty()){
                for (Integer playlistId: listRemove) {
                    Response<Void> deleteResponse = playlistItemService.deleteWithInformationMovie(
                            userId, playlistId, informationMovie.getMovieId(), informationMovie.getTag(),
                            "Bearer "+token).execute();
                    if(deleteResponse.code() == 200){
                        responseMessage.add(removeFromString + playlistId + successfullyString);
                    }else {
                        responseMessage.add(removeFromString + playlistId + failedString);
                    }
                }
            }
            return responseMessage;
        } catch (Exception e) {
            Log.e("LOAD_BACKGROUND_ERROR", "error", e);
        }
        return null;
    }
}