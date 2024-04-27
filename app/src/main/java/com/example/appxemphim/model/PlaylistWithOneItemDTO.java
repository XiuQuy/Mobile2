package com.example.appxemphim.model;

public class PlaylistWithOneItemDTO {
    private int userId;
    private String title;
    private PlaylistItem item;

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

    public PlaylistItem getItem() {
        return item;
    }

    public void setItem(PlaylistItem item) {
        this.item = item;
    }
}
