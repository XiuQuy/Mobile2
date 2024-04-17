package com.example.appxemphim.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.appxemphim.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel extends ViewModel {
    List<Playlist> listPlaylist;

    public List<Playlist> getListPlaylist() {
        return listPlaylist;
    }

    public void setListPlaylist(List<Playlist> listPlaylist) {
        this.listPlaylist = listPlaylist;
    }
}
