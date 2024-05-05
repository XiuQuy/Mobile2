package com.example.appxemphim.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TMDBMovieResult {
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
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("title")
    private String title;
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
        movie.setId(String.valueOf(this.id));
        movie.setGenreIds(this.genreIds);
        movie.setOriginalLanguage(this.originalLanguage);
        movie.setPosterPath("https://image.tmdb.org/t/p/w500/"+this.posterPath);
        movie.setReleaseDate(this.releaseDate);
        movie.setName(this.title);
        movie.setVoteAverage(this.voteAverage);
        movie.setVoteCount(this.voteCount);
        movie.setTag("TMDB_MOVIE");
        return movie;
    }

    public Movie toMovie1(){
        Movie movie = new Movie();
        movie.setAdult(this.adult);
        movie.setId(String.valueOf(this.id));
        movie.setGenreIds(this.genreIds);
        movie.setOriginalLanguage(this.originalLanguage);
        movie.setPosterPath("https://image.tmdb.org/t/p/w500/"+this.posterPath);
        movie.setName(this.name);
        movie.setVoteAverage(this.voteAverage);
        movie.setVoteCount(this.voteCount);
        movie.setTag("TMDB_TV_SERIES");
        return movie;
    }


    public static List<Movie> toListMovie(List<TMDBMovieResult> dataResults) {
        List<Movie> movies = new ArrayList<>();
        for (TMDBMovieResult dataResult : dataResults) {
            movies.add(dataResult.toMovie());
        }
        return movies;
    }
    public static List<Movie> toListTVShow(List<TMDBMovieResult> dataResults) {
        List<Movie> movies = new ArrayList<>();
        for (TMDBMovieResult dataResult : dataResults) {
            movies.add(dataResult.toMovie1());
        }
        return movies;
    }

}
