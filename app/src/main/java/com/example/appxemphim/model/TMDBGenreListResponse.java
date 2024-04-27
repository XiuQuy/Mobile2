package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TMDBGenreListResponse {
    @SerializedName("genres")
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }
}
