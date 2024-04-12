package com.example.appxemphim;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {
    // Annotation @SerializedName được sử dụng để ánh xạ các trường trong JSON với các trường trong lớp Java

    @SerializedName("id")
    private int id; // ID của phim

    @SerializedName("title")
    private String title; // Tiêu đề của phim

    @SerializedName("overview")
    private String overview; // Tóm tắt về phim

    @SerializedName("poster_path")
    private String posterPath; // Đường dẫn đến poster của phim

    @SerializedName("release_date")
    private String releaseDate; // Ngày phát hành của phim

    @SerializedName("vote_average")
    private double rating; // Đánh giá của phim

    // Constructor để khởi tạo một đối tượng Movie

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double rating) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    // Getters và Setters để truy xuất và thiết lập các giá trị của đối tượng Movie

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}