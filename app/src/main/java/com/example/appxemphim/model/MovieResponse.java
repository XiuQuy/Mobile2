package com.example.appxemphim.model;

import java.util.List;

/**
 * Lớp đại diện cho phản hồi từ API TMDB chứa danh sách các phim.
 */
public class MovieResponse {

    // Danh sách các phim nhận được từ phản hồi API
    private List<Movie> results;
    /**
     * Lấy danh sách các phim.
     * @return Danh sách các phim
     */
    public List<Movie> getResults() {
        return results;
    }


    /**
     * Thiết lập danh sách các phim.
     * @param results Danh sách các phim
     */
    public void setResults(List<Movie> results) {
        this.results = results;
    }

}
