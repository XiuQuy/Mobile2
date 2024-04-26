package com.example.appxemphim.model;

public class ReviewVideo {
    private int id;
    private String movieId;
    private InformationMovie informationReviewVideo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public InformationMovie getInformationReviewVideo() {
        return informationReviewVideo;
    }

    public void setInformationReviewVideo(InformationMovie informationReviewVideo) {
        this.informationReviewVideo = informationReviewVideo;
    }
}
