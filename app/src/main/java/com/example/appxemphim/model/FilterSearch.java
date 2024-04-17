package com.example.appxemphim.model;

import java.util.HashSet;
import java.util.Set;

public class FilterSearch {
    private boolean adult = false;
    private String year = "";
    private Set<Integer> genreIds = new HashSet<>();

    public FilterSearch(boolean adult, String year, Set<Integer> genreIds) {
        this.adult = adult;
        this.year = year;
        this.genreIds = genreIds;
    }

    public FilterSearch() {
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Set<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public void setDefault(){
        this.adult = false;
        this.year = "";
        this.genreIds = new HashSet<>();
    }
}
