package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TMDBSearchMovieResponse {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<TMDBMovieResult> results;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;

    public int getPage() {
        return page;
    }

    public List<TMDBMovieResult> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
