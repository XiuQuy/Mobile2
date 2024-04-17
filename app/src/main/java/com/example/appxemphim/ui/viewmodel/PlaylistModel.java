package com.example.appxemphim.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.appxemphim.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel extends ViewModel {
    List<Playlist> listPlaylist;

    public List<Playlist> getListPlaylist() {
        listPlaylist = new ArrayList<>();
        Playlist p = new Playlist();
        p.setId(1);
        p.setTitle("Playlist1");
        Playlist p1 = new Playlist();
        p1.setId(2);
        p1.setTitle("Playlist2");
        Playlist p2 = new Playlist();
        p2.setId(3);
        p2.setTitle("Playlist3");
        Playlist p3 = new Playlist();
        p3.setId(4);
        p3.setTitle("Playlist4");
        listPlaylist.add(p);
        listPlaylist.add(p1);
        listPlaylist.add(p2);
        listPlaylist.add(p3);
        return listPlaylist;
    }

    public void setListPlaylist(List<Playlist> listPlaylist) {
        this.listPlaylist = listPlaylist;
    }
}
