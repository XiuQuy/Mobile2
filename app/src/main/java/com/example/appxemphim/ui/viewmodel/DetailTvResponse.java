package com.example.appxemphim.ui.viewmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailTvResponse {
    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;
    @SerializedName("adult")
    private boolean adult;
//    @SerializedName("episode_run_time")
//    private int episode_run_time;
    @SerializedName("popularity")
    private double popularity;
    @SerializedName("budget")
    private long budget;
    @SerializedName("seasons")
    private List<Season> seasons;
    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;
    @SerializedName("episode_run_time")
    private List<Integer> episodeRunTime;

    @SerializedName("first_air_date")
    private String firstAirDate;
    public String getName() {
        return name;
    }
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("status")
    private String status;
    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spokenLanguages;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("revenue")
    private long revenue;
    @SerializedName("homepage")
    private String homepage;
    public String getHomepage() {
        return homepage;
    }
    public List<Season> getSeasons() {
        return seasons;
    }
    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }
    public Integer getFirstEpisodeRunTime() {
        if (episodeRunTime != null && !episodeRunTime.isEmpty()) {
            return episodeRunTime.get(0);
        } else {
            return null;
        }
    }
public class Season {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("episode_count")
    private int episodeCount;

    @SerializedName("air_date")
    private String airDate;

    @SerializedName("vote_average")
    private float rating;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    public Season(int id, String name, int episodeCount, String airDate, float rating, String overview, String posterPath) {
        this.id = id;
        this.name = name;
        this.episodeCount = episodeCount;
        this.airDate = airDate;
        this.rating = rating;
        this.overview = overview;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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
}
public long getBudget() {
    return budget;
}
    public long getRevenue() {
        return revenue;
    }

    public class Genre {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
//    public EpisodeRunTime getEpisodeRunTime() {
//        return episodeRunTime;
//    }
//
//    public void setEpisodeRunTime(EpisodeRunTime episodeRunTime) {
//        this.episodeRunTime = episodeRunTime;
//    }
    public double getPopularity() {
    return popularity;
}
    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }
    public class SpokenLanguage {
        @SerializedName("english_name")
        private String englishName;


        @SerializedName("name")
        private String name;

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public boolean isAdult() {
        return adult;
    }
    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }
    public class ProductionCompany {
        @SerializedName("id")
        private int id;

        @SerializedName("logo_path")
        private String logoPath;

        @SerializedName("name")
        private String name;

        @SerializedName("origin_country")
        private String originCountry;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogoPath() {
            return logoPath;
        }

        public void setLogoPath(String logoPath) {
            this.logoPath = logoPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOriginCountry() {
            return originCountry;
        }

        public void setOriginCountry(String originCountry) {
            this.originCountry = originCountry;
        }
    }
    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }
    public String getFormattedGenres() {
        if (genres != null && genres.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Genre genre : genres) {
                stringBuilder.append(genre.getName()).append(", ");
            }
            // Loại bỏ dấu phẩy cuối cùng
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 2);
            }
            return stringBuilder.toString();
        }
        return "";
    }
    public String getFormattedSpokenLanguages() {
        if (spokenLanguages != null && spokenLanguages.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (SpokenLanguage language : spokenLanguages) {
                stringBuilder.append(language.getName()).append(" - ");
            }
            // Loại bỏ dấu '*' cuối cùng và khoảng trắng thừa
            stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
            return stringBuilder.toString();
        }
        return "";
    }
}
