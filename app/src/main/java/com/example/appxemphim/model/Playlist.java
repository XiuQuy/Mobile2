package com.example.appxemphim.model;

import java.util.List;

public class Playlist {
    private int id;
    private int userId;
    private String title;
    private int itemCount;
    private List<PlaylistItem> playlistItems;

    public Playlist(int id, int userId, String title, int itemCount, List<PlaylistItem> playlistItems) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.itemCount = itemCount;
        this.playlistItems = playlistItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public List<PlaylistItem> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(List<PlaylistItem> playlistItems) {
        this.playlistItems = playlistItems;
    }
}

