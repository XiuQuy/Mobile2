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

    public void  addAPlaylist(Playlist playlist){
        listPlaylist.add(0, playlist);
    }

    public void moveToTop(Integer id) {
        for (int i = 0; i < listPlaylist.size(); i++) {
            Playlist playlist = listPlaylist.get(i);
            if (playlist.getId() == id) {
                listPlaylist.remove(i);
                listPlaylist.add(0, playlist);
                break;
            }
        }
    }

}
