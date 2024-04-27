package com.example.appxemphim.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.appxemphim.model.FilterSearch;
import com.example.appxemphim.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private List<Movie> movies;
    private int page;
    private FilterSearch filterSearch;
    private boolean isSearchTVNoData = false;
    private boolean isSearchMovieNoData = false;

    public List<Movie> getMovies() {
        if (movies == null) {
            movies = new ArrayList<>();
        }
        return movies;
    }

    public void setMovies(List<Movie> itemList) {
        this.movies = itemList;
    }

    public void addMovie(Movie movie){
        movies.add(movie);
    }

    public void clearMovies(){
        if (movies != null) {
            movies.clear();
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int plusOnePage() {
        this.page += 1;
        return page;
    }

    public FilterSearch getFilterSearch() {
        if(filterSearch == null){
            return new FilterSearch();
        }
        return filterSearch;
    }

    public void setFilterSearch(FilterSearch filterSearch) {
        this.filterSearch = filterSearch;
    }

    public boolean isSearchTVNoData() {
        return isSearchTVNoData;
    }

    public void setSearchTVNoData(boolean searchTVNoData) {
        isSearchTVNoData = searchTVNoData;
    }

    public boolean isSearchMovieNoData() {
        return isSearchMovieNoData;
    }

    public void setSearchMovieNoData(boolean searchMovieNoData) {
        isSearchMovieNoData = searchMovieNoData;
    }
}
