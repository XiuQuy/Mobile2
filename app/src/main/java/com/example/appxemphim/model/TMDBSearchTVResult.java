package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TMDBSearchTVResult {
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("id")
    private long id;
    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("first_air_date")
    private Date firstAirDate;
    @SerializedName("name")
    private String name;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private long voteCount;

    public Movie toMovie(){
        Movie movie = new Movie();
        movie.setAdult(this.adult);
        movie.setId(this.id);
        movie.setGenreIds(this.genreIds);
        movie.setOriginalLanguage(this.originalLanguage);
        movie.setPosterPath("https://image.tmdb.org/t/p/w500/"+this.posterPath);
        movie.setReleaseDate(this.firstAirDate);
        movie.setName(this.name);
        movie.setVoteAverage(this.voteAverage);
        movie.setVoteCount(this.voteCount);
        movie.setTag("TMDB_TV_SERIES");
        return movie;
    }

    public static List<Movie> toListMovie(List<TMDBSearchTVResult> dataResults) {
        List<Movie> movies = new ArrayList<>();
        for (TMDBSearchTVResult dataResult : dataResults) {
            movies.add(dataResult.toMovie());
        }
        return movies;
    }
}
