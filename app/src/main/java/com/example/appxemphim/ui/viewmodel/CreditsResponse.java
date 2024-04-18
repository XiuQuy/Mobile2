package com.example.appxemphim.ui.viewmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class CreditsResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("cast")
    private List<Cast> cast;

    @SerializedName("crew")
    private List<Crew> crew;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public void setCrew(List<Crew> crew) {
        this.crew = crew;
    }

    public static class Cast {
        @SerializedName("profile_path")
        private String profilePath;
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("character")
        private String character;

        public Cast(int id, String name, String character) {
            this.id = id;
            this.name = name;
            this.character = character;
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

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getImageUrl() {
            return "https://image.tmdb.org/t/p/w500" + profilePath;
        }
    }
    public static class Crew {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("job")
        private String job;
        @SerializedName("profile_path")
        private String profilePath;

        public Crew(int id, String name, String job) {
            this.id = id;
            this.name = name;
            this.job = job;
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

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }
        public String getImageUrl() {
            return profilePath;
        }
    }
}
